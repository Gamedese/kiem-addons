"""Compile the real KiemConfig with tiny external stubs; test fresh-JVM persistence.

Uses only build/config-persistence-* fixtures, never the user's Minecraft config.
Run: python tools/verify_config_persistence.py [--java-home PATH]
"""
from pathlib import Path
import argparse
import os
import shutil
import subprocess
import tempfile


ROOT = Path(__file__).resolve().parents[1]
STUBS = {
    'com/mojang/blaze3d/platform/InputConstants.java': '''
package com.mojang.blaze3d.platform;
public class InputConstants {
 public static final int KEY_Z=90, KEY_C=67, KEY_F6=295, KEY_V=86;
 public static final Key UNKNOWN=new Key();
 public static boolean isKeyDown(Object window,int key) { return false; }
 public static class Key {
  public int getValue() { return -1; }
  public Text getDisplayName() { return new Text(); }
 }
 public static class Text { public String getString() { return "stub"; } }
 public enum Type { KEYSYM; public Key getOrCreate(int key) { return new Key(); } }
}''',
    'net/minecraft/client/Minecraft.java': '''
package net.minecraft.client;
public class Minecraft {
 public static Minecraft getInstance() { return null; }
 public Object getWindow() { return null; }
}''',
    'net/fabricmc/loader/api/FabricLoader.java': '''
package net.fabricmc.loader.api;
import java.nio.file.*;
public class FabricLoader {
 public static FabricLoader getInstance() { return new FabricLoader(); }
 public Path getConfigDir() { return Path.of(System.getProperty("fixture.config")); }
}''',
    'com/example/client/KiemCompat.java': '''
package com.example.client;
public class KiemCompat {
 public static Object getScreen(net.minecraft.client.Minecraft mc) { return null; }
}''',
    'com/example/client/CapeManager.java': '''
package com.example.client;
public class CapeManager {
 public enum CapeType { OFF, SANARA_ANIMATED, SANARA_STATIC, SANARA_V2_ANIMATED }
 private static CapeType current=CapeType.SANARA_V2_ANIMATED;
 public static CapeType getCurrentCape() { return current; }
 public static void setCurrentCapeDirect(CapeType cape) { current=cape; }
 public static void setCurrentCape(CapeType cape) { current=cape; }
}''',
    'VerifyConfigPersistence.java': '''
import com.example.client.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
public class VerifyConfigPersistence {
 private static final String DEFAULTS="0.8,1,1,0.63,0.954,1.278,0.74,0.84,0.79";
 private static final String[] KEYS={"scale","speed","bob","height.0","height.1","height.2","radius.0","radius.1","radius.2"};
 private static void check(boolean pass,String message) {
  if (!pass) throw new AssertionError(message);
 }
 private static void customization(String expected,int petals) {
  float[] actual={KiemConfig.sakuraAuraScale,KiemConfig.sakuraAuraSpeed,KiemConfig.sakuraAuraBob,
   KiemConfig.sakuraAuraHeights[0],KiemConfig.sakuraAuraHeights[1],KiemConfig.sakuraAuraHeights[2],
   KiemConfig.sakuraAuraRadii[0],KiemConfig.sakuraAuraRadii[1],KiemConfig.sakuraAuraRadii[2]};
  String[] values=expected.split(",");
  for (int i=0;i<actual.length;i++) {
   check(Float.isFinite(actual[i]) && Math.abs(actual[i]-Float.parseFloat(values[i]))<0.00001f,
    "Customization not restored: "+KEYS[i]+" actual="+actual[i]+" expected="+values[i]);
  }
  check(KiemConfig.sakuraAuraPetals==petals,"Petal density not restored");
 }
 private static void savedCustomization(Properties props,String expected,int petals) {
  String[] values=expected.split(",");
  for (int i=0;i<KEYS.length;i++) {
   String value=props.getProperty("aura.sakura_lop."+KEYS[i]);
   check(value!=null && Math.abs(Float.parseFloat(value)-Float.parseFloat(values[i]))<0.00001f,
    "Customization not saved: "+KEYS[i]);
  }
  check(props.getProperty("aura.sakura_lop.petals").equals(String.valueOf(petals)),"Petals not saved");
 }
 private static Properties read(Path file) throws Exception {
  Properties props=new Properties();
  try (Reader reader=Files.newBufferedReader(file)) { props.load(reader); }
  return props;
 }
 public static void main(String[] args) throws Exception {
  boolean existing=args[0].equals("existing");
  boolean aura=args[1].equals("true");
  String expected=args[2];
  int petals=Integer.parseInt(args[3]);
  Path file=Path.of(System.getProperty("fixture.config"),"kiem.properties");
  byte[] before=Files.exists(file) ? Files.readAllBytes(file) : null;
  // First reference triggers the actual static initializer, without calling load twice.
  check(KiemConfig.sakuraAuraEnabled==aura,"Aura not restored on first class load");
  check(KiemConfig.recentModules!=null,"Recents not initialized");
  customization(expected,petals);
  if (existing) {
   check(Arrays.equals(before,Files.readAllBytes(file)),"Loading modified the config file");
   check(KiemConfig.recentModules.equals(List.of("smooth_zoom","chat_qol")),"Recents lost");
   check(KiemConfig.shoulderPetType.equals("HAMSTER_HD"),"Late pet key not restored");
   check(KiemConfig.shoulderPetSide.equals("LEFT"),"Pet side not restored");
   check(!KiemConfig.inventoryCurseWithSword,"Pet sword flag not restored");
   check(KiemConfig.dragonVariant.equals("ENDER"),"Dragon variant not restored");
   check(CapeManager.getCurrentCape()==CapeManager.CapeType.SANARA_STATIC,"Cape not restored");
   check(KiemConfig.customCapeFileName.equals("fixture-cape.gif"),"Custom cape not restored");
   check(!KiemConfig.capeTrueColor,"Cape flag not restored");
   check(KiemConfig.language.equals("en") && KiemConfig.hasSelectedLanguage,"Language not restored");
  }
  if (args.length>4 && args[4].equals("reset")) {
   byte[] beforeReset=Files.readAllBytes(file);
   KiemConfig.resetSakuraAuraSettings();
   check(KiemConfig.sakuraAuraEnabled==aura,"Customization reset changed enabled state");
   check(Arrays.equals(beforeReset,Files.readAllBytes(file)),"Customization reset saved without caller request");
   expected=DEFAULTS;
   petals=2;
   customization(expected,petals);
  }
  KiemConfig.save();
  Properties props=read(file);
  check(props.size()>=75,"Config truncated or incompletely written: "+props.size());
  savedCustomization(props,expected,petals);
  check(props.getProperty("aura.sakura_lop").equals(String.valueOf(aura)),"Aura not saved");
  check(props.getProperty("pet.type").equals(existing ? "HAMSTER_HD" : "OFF"),"Pet not saved");
  check(props.containsKey("recent_modules") && props.containsKey("cape"),"Tail keys missing");
  if (existing) {
   check(props.getProperty("recent_modules").equals("smooth_zoom,chat_qol"),"Recents not saved");
   check(props.getProperty("cape").equals("SANARA_STATIC"),"Cape not saved");
   check(props.getProperty("cape.custom_name").equals("fixture-cape.gif"),"Custom cape not saved");
  }
  if (args.length>4 && args[4].equals("fullreset")) {
   KiemConfig.resetToDefaults();
   check(!KiemConfig.sakuraAuraEnabled,"Full reset left aura enabled");
   customization(DEFAULTS,2);
   savedCustomization(read(file),DEFAULTS,2);
   check(read(file).getProperty("aura.sakura_lop").equals("false"),"Full reset not saved");
  }
  System.out.println("PASS "+String.join(" ",args)+" keys="+props.size());
 }
}''',
}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--java-home', default=os.environ.get('JAVA_HOME'))
    args = parser.parse_args()
    suffix = '.exe' if os.name == 'nt' else ''
    java = str(Path(args.java_home) / 'bin' / ('java' + suffix)) if args.java_home else 'java'
    javac = str(Path(args.java_home) / 'bin' / ('javac' + suffix)) if args.java_home else 'javac'
    build = ROOT / 'build'
    build.mkdir(exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='config-persistence-', dir=build) as temporary:
        temp = Path(temporary)
        sources = temp / 'sources'
        classes = temp / 'classes'
        classes.mkdir()
        for relative, content in STUBS.items():
            path = sources / relative
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content, encoding='utf-8')
        actual = sources / 'com/example/client/KiemConfig.java'
        shutil.copyfile(ROOT / 'src/client/java/com/example/client/KiemConfig.java', actual)
        subprocess.run([javac, '-encoding', 'UTF-8', '-d', str(classes),
                        *map(str, sources.rglob('*.java'))], check=True)
        fixture = '''language=en
has_selected_language=true
recent_modules=smooth_zoom,chat_qol
cape=SANARA_STATIC
cape.custom_name=fixture-cape.gif
cape.true_color=false
pet.type=HAMSTER_HD
pet.side=LEFT
pet.inventory_curse_sword=false
pet.dragon_variant=ENDER
'''
        keys = ('scale', 'speed', 'bob', 'height.0', 'height.1', 'height.2',
                'radius.0', 'radius.1', 'radius.2', 'petals')
        defaults = ('0.8', '1', '1', '0.63', '0.954', '1.278', '0.74', '0.84', '0.79', '2')
        custom = ('1.15', '1.7', '2.3', '0.42', '1.13', '1.87', '0.61', '1.11', '1.37', '3')

        def values_csv(values):
            return ','.join(values[:9])

        def run(config, enabled, expected, mode='existing', action=None):
            command = [java, f'-Dfixture.config={config}', '-cp', str(classes),
                       'VerifyConfigPersistence', mode, str(enabled).lower(),
                       values_csv(expected), expected[9]]
            if action:
                command.append(action)
            subprocess.run(command, check=True)

        def seed(name, enabled, settings):
            config = temp / name
            config.mkdir()
            properties = fixture + f'aura.sakura_lop={str(enabled).lower()}\n'
            properties += ''.join(f'aura.sakura_lop.{key}={value}\n'
                                  for key, value in zip(keys, settings))
            (config / 'kiem.properties').write_text(properties, encoding='utf-8')
            return config

        for enabled in (True, False):
            config = seed('enabled' if enabled else 'disabled', enabled, custom)
            # Separate JVMs prove first initialization and restart after saving.
            run(config, enabled, custom)
            run(config, enabled, custom)
        config = seed('legacy-missing', True, ())
        run(config, True, defaults)
        run(config, True, defaults)
        invalid = ('NaN', 'Infinity', 'broken', '-Infinity', 'NaN', 'broken',
                   'Infinity', 'broken', '1e100', 'NaN')
        config = seed('invalid', True, invalid)
        run(config, True, defaults)
        run(config, True, defaults)
        outside = ('10', '-10', '10', '-1', '10', '0.77', '-1', '10', '0.92', '7')
        clamped = ('1.4', '0.25', '3', '0.15', '2.2', '0.77', '0.4', '1.5', '0.92', '3')
        config = seed('outside', True, outside)
        run(config, True, clamped)
        run(config, True, clamped)
        # One broken value cannot stop valid neighbors or reset their values.
        mixed = ('1.15', 'NaN', '2.3', '0.42', 'bad', '1.87', '0.61', '1.11', 'Infinity', '-9')
        expected = ('1.15', '1', '2.3', '0.42', '0.954', '1.87', '0.61', '1.11', '0.79', '0')
        config = seed('mixed', True, mixed)
        run(config, True, expected)
        run(config, True, expected)
        config = seed('reset-customization', True, custom)
        run(config, True, custom, action='reset')
        run(config, True, defaults)
        config = seed('reset-all', True, custom)
        run(config, True, custom, action='fullreset')
        run(config, False, defaults, mode='fresh')
        config = temp / 'fresh'
        run(config, False, defaults, mode='fresh')
        run(config, False, defaults, mode='fresh')
    print('All fresh-JVM config persistence checks passed.')


if __name__ == '__main__':
    main()
