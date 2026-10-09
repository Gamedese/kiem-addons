package com.example.client;

import java.util.HashMap;
import java.util.Map;

public class KiemLang {

    public enum Language {
        TR("tr", "Türkçe", "🇹🇷"),
        EN("en", "English", "🇬🇧");

        private final String code;
        private final String displayName;
        private final String flag;

        Language(String code, String displayName, String flag) {
            this.code = code;
            this.displayName = displayName;
            this.flag = flag;
        }

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getFlag() {
            return flag;
        }

        public static Language fromCode(String code) {
            if ("en".equalsIgnoreCase(code)) return EN;
            return TR;
        }
    }

    private static final Map<String, String> TR_MAP = new HashMap<>();
    private static final Map<String, String> EN_MAP = new HashMap<>();

    static {
        // --- COMMON UI ---
        tr("ui.back", "← Geri");
        en("ui.back", "← Back");
        tr("ui.reset", "Sıfırla");
        en("ui.reset", "Reset");
        tr("ui.close", "✕ Kapat");
        en("ui.close", "✕ Close");
        tr("ui.customize", "Özelleştir →");
        en("ui.customize", "Customize →");
        tr("ui.edit", "Düzenle →");
        en("ui.edit", "Edit →");
        tr("ui.inspect", "İncele →");
        en("ui.inspect", "Inspect →");
        tr("ui.on", "AÇIK");
        en("ui.on", "ON");
        tr("ui.off", "KAPALI");
        en("ui.off", "OFF");
        tr("ui.press_key", "Tuşa bas...");
        en("ui.press_key", "Press key...");
        tr("ui.none", "Yok");
        en("ui.none", "None");
        tr("ui.cancel", "✕ İptal");
        en("ui.cancel", "✕ Cancel");
        tr("ui.confirm", "✓ Sıfırla");
        en("ui.confirm", "✓ Reset");
        tr("ui.modules", "Modüller");
        en("ui.modules", "Modules");

        // --- SIDEBAR TABS ---
        tr("tab.welcome", "Welcome");
        en("tab.welcome", "Welcome");
        tr("tab.visuals", "Visual Stuffs");
        en("tab.visuals", "Visual Stuffs");
        tr("tab.features", "Features");
        en("tab.features", "Features");
        tr("tab.cosmetics", "Cosmetics");
        en("tab.cosmetics", "Cosmetics");
        tr("sidebar.main", "MAIN");
        en("sidebar.main", "MAIN");
        tr("sidebar.config", "CONFIGURATION");
        en("sidebar.config", "CONFIGURATION");

        // --- LANGUAGE SELECT MODAL ---
        tr("lang.modal_title", "Select Language / Dil Seçimi");
        en("lang.modal_title", "Select Language");
        tr("lang.modal_desc", "Please select your preferred language for Kiem Addons.");
        en("lang.modal_desc", "Please select your preferred language for Kiem Addons.");
        tr("lang.modal_desc2", "You can change this anytime from the bottom-left of the menu.");
        en("lang.modal_desc2", "You can change this anytime from the bottom-left of the menu.");
        tr("lang.btn_tr", "Türkçe (TR)");
        en("lang.btn_tr", "Türkçe (TR)");
        tr("lang.btn_en", "English (EN)");
        en("lang.btn_en", "English (EN)");

        // --- WELCOME TAB ---
        tr("welcome.hero_title", "✦ Kiem Addons");
        en("welcome.hero_title", "✦ Kiem Addons");
        tr("welcome.hero_desc", "Hoş geldin! Kişiselleştirilmiş, optimize ve zenginleştirilmiş Minecraft deneyimi.");
        en("welcome.hero_desc", "Welcome! Personalized, optimized and enhanced Minecraft experience.");
        tr("welcome.menu_key", "Menü Kısayolu:");
        en("welcome.menu_key", "Menu Keybind:");
        tr("welcome.reset_all", "⟲ Varsayılana Sıfırla");
        en("welcome.reset_all", "⟲ Reset to Defaults");
        tr("welcome.recent_title", "⚡ Son Düzenlenen Ayarlar / Eklentiler");
        en("welcome.recent_title", "⚡ Recently Modified Settings / Modules");
        tr("welcome.nav_features", "⚙ Features Menüsü");
        en("welcome.nav_features", "⚙ Features Menu");
        tr("welcome.nav_visuals", "🎨 Visual Stuffs Menüsü");
        en("welcome.nav_visuals", "🎨 Visual Stuffs Menu");
        tr("welcome.nav_cosmetics", "✨ Pelerinler (Cosmetics)");
        en("welcome.nav_cosmetics", "✨ Capes (Cosmetics)");

        // --- RESET MODAL ---
        tr("reset_modal.title", "⚠ Tüm Ayarları Sıfırla");
        en("reset_modal.title", "⚠ Reset All Settings");
        tr("reset_modal.desc1", "Tüm Kiem Addons ayarlarını varsayılana");
        en("reset_modal.desc1", "Are you sure you want to restore all Kiem Addons");
        tr("reset_modal.desc2", "döndürmek istediğinize emin misiniz?");
        en("reset_modal.desc2", "settings to their default values?");
        tr("reset_modal.desc3", "Bu işlem geri alınamaz.");
        en("reset_modal.desc3", "This action cannot be undone.");

        // --- MODULE TITLES & DESCRIPTIONS ---
        // Mini Models
        tr("mod.mini_models.title", "Küçük Adamlar");
        en("mod.mini_models.title", "Mini Models");
        tr("mod.mini_models.desc", "Kendini veya diğer oyuncuları X/Y/Z boyutlandırma & kamera göz seviyesi ayarı.");
        en("mod.mini_models.desc", "Scale self and other players on X/Y/Z axes & adjust eye height.");
        tr("mod.mini_models.self_title", "Kendini Küçült (Self)");
        en("mod.mini_models.self_title", "Self Scale (Self)");
        tr("mod.mini_models.self_desc", "Kendi oyuncu modelini boyutlandırır.");
        en("mod.mini_models.self_desc", "Scales your own player model.");
        tr("mod.mini_models.camera_title", "Kamerayı Etkile");
        en("mod.mini_models.camera_title", "Affect Camera");
        tr("mod.mini_models.camera_desc", "Kamera göz hizası Y ile birlikte aşağı iner.");
        en("mod.mini_models.camera_desc", "Camera eye height shifts with Y scale.");
        tr("mod.mini_models.others_title", "Diğerlerini Küçült (Others)");
        en("mod.mini_models.others_title", "Others Scale (Others)");
        tr("mod.mini_models.others_desc", "Diğer oyuncuların modelini boyutlandırır.");
        en("mod.mini_models.others_desc", "Scales other players' models.");
        tr("mod.mini_models.x_title", "X Boyutu");
        en("mod.mini_models.x_title", "X Scale");
        tr("mod.mini_models.x_sub", "Genişlik");
        en("mod.mini_models.x_sub", "Width");
        tr("mod.mini_models.y_title", "Y Boyutu");
        en("mod.mini_models.y_title", "Y Scale");
        tr("mod.mini_models.y_sub", "Yükseklik");
        en("mod.mini_models.y_sub", "Height");
        tr("mod.mini_models.z_title", "Z Boyutu");
        en("mod.mini_models.z_title", "Z Scale");
        tr("mod.mini_models.z_sub", "Derinlik");
        en("mod.mini_models.z_sub", "Depth");

        // Custom Viewmodel
        tr("mod.viewmodel.title", "Custom Viewmodel & Vuruş Yavaşlatıcı");
        en("mod.viewmodel.title", "Custom Viewmodel & Swing Slowdown");
        tr("mod.viewmodel.desc", "Eşya boyutu, pozisyon, rotasyon açıları & CPS düşürmeden ağır çekim vuruş animasyonu.");
        en("mod.viewmodel.desc", "Held item scale, position, rotation & cinematic swing animation without CPS loss.");
        tr("mod.viewmodel.size_title", "Boyut (Size)");
        en("mod.viewmodel.size_title", "Item Size");
        tr("mod.viewmodel.size_sub", "Eldeki eşyanın ölçeği.");
        en("mod.viewmodel.size_sub", "Held item scale.");
        tr("mod.viewmodel.scale_swing_title", "Vuruşu Ölçekle");
        en("mod.viewmodel.scale_swing_title", "Scale Swing");
        tr("mod.viewmodel.scale_swing_sub", "Vuruş animasyonu boyutunu da ölçekler.");
        en("mod.viewmodel.scale_swing_sub", "Also scales swing animation size.");
        tr("mod.viewmodel.x_title", "X Pozisyonu");
        en("mod.viewmodel.x_title", "X Position");
        tr("mod.viewmodel.x_sub", "Yatay kaydırma.");
        en("mod.viewmodel.x_sub", "Horizontal offset.");
        tr("mod.viewmodel.y_title", "Y Pozisyonu");
        en("mod.viewmodel.y_title", "Y Position");
        tr("mod.viewmodel.y_sub", "Dikey kaydırma.");
        en("mod.viewmodel.y_sub", "Vertical offset.");
        tr("mod.viewmodel.z_title", "Z Pozisyonu");
        en("mod.viewmodel.z_title", "Z Position");
        tr("mod.viewmodel.z_sub", "İleri / geri kaydırma.");
        en("mod.viewmodel.z_sub", "Depth offset.");
        tr("mod.viewmodel.yaw_title", "Yaw (Dönüş)");
        en("mod.viewmodel.yaw_title", "Yaw Rotation");
        tr("mod.viewmodel.yaw_sub", "Y ekseni dönüş açısı.");
        en("mod.viewmodel.yaw_sub", "Y-axis angle.");
        tr("mod.viewmodel.pitch_title", "Pitch (Eğim)");
        en("mod.viewmodel.pitch_title", "Pitch Rotation");
        tr("mod.viewmodel.pitch_sub", "X ekseni eğim açısı.");
        en("mod.viewmodel.pitch_sub", "X-axis angle.");
        tr("mod.viewmodel.roll_title", "Roll (Yatış)");
        en("mod.viewmodel.roll_title", "Roll Rotation");
        tr("mod.viewmodel.roll_sub", "Z ekseni yatış açısı.");
        en("mod.viewmodel.roll_sub", "Z-axis angle.");
        tr("mod.viewmodel.speed_title", "Vuruş Hızı");
        en("mod.viewmodel.speed_title", "Swing Speed");
        tr("mod.viewmodel.speed_sub", "Ağır çekim vuruş animasyonu.");
        en("mod.viewmodel.speed_sub", "Cinematic swing slowdown.");
        tr("mod.viewmodel.noskip_title", "Animasyonu Atlatma");
        en("mod.viewmodel.noskip_title", "Don't Skip Swing");
        tr("mod.viewmodel.noskip_sub", "Hızlı tıklamalarda animasyonun sıfırlanmasını engeller.");
        en("mod.viewmodel.noskip_sub", "Prevents swing resetting on rapid clicks.");

        // Better Third Person
        tr("mod.better_third_person.title", "Better Third Person");
        en("mod.better_third_person.title", "Better Third Person");
        tr("mod.better_third_person.desc", "Dinamik hız gecikmesi, omuz bakışı ve yumuşak kamera takibi.");
        en("mod.better_third_person.desc", "Dynamic speed lag, shoulder surfing and smooth camera tracking.");
        tr("mod.better_third_person.row1_title", "Better Third Person Modülü");
        en("mod.better_third_person.row1_title", "Better Third Person Module");
        tr("mod.better_third_person.row1_desc", "3. şahıs kamerasında dinamik takip ve omuz görünümü.");
        en("mod.better_third_person.row1_desc", "Dynamic tracking and shoulder view in 3rd person.");
        tr("mod.better_third_person.shoulder_title", "Shoulder Surfing (Omuz Modu)");
        en("mod.better_third_person.shoulder_title", "Shoulder Surfing");
        tr("mod.better_third_person.shoulder_desc", "Kamerayı karakterin sağına veya soluna ofsetler.");
        en("mod.better_third_person.shoulder_desc", "Offsets camera to character's right or left.");
        tr("mod.better_third_person.swap_title", "Omuz Değiştirme Kısayolu");
        en("mod.better_third_person.swap_title", "Shoulder Swap Keybind");
        tr("mod.better_third_person.left", "Sol");
        en("mod.better_third_person.left", "Left");
        tr("mod.better_third_person.right", "Sağ");
        en("mod.better_third_person.right", "Right");
        tr("mod.better_third_person.smooth_title", "Kamera Yumuşaklığı");
        en("mod.better_third_person.smooth_title", "Camera Smoothness");
        tr("mod.better_third_person.smooth_sub", "Geçiş yumuşaklığı");
        en("mod.better_third_person.smooth_sub", "Interpolation factor");
        tr("mod.better_third_person.lag_title", "Hız Gecikmesi (Speed Lag)");
        en("mod.better_third_person.lag_title", "Speed Lag");
        tr("mod.better_third_person.lag_desc", "Koşarken kamera geriden takip ederek hız hissi katar.");
        en("mod.better_third_person.lag_desc", "Camera lags behind when sprinting for speed feel.");
        tr("mod.better_third_person.dist_title", "Kamera Mesafesi");
        en("mod.better_third_person.dist_title", "Camera Distance");
        tr("mod.better_third_person.dist_sub", "Mesafe (blok)");
        en("mod.better_third_person.dist_sub", "Distance (blocks)");

        // Smooth Zoom
        tr("mod.smooth_zoom.title", "Smooth Zoom");
        en("mod.smooth_zoom.title", "Smooth Zoom");
        tr("mod.smooth_zoom.desc", "Yumuşak geçişli yakınlaştırma, scroll zoom ve sinematik kamera buz efekti.");
        en("mod.smooth_zoom.desc", "Smooth zoom transition, mouse wheel zoom and cinematic ice camera.");
        tr("mod.smooth_zoom.row1_title", "Smooth Zoom Modülü");
        en("mod.smooth_zoom.row1_title", "Smooth Zoom Module");
        tr("mod.smooth_zoom.row1_desc", "Tuşa basıldığında akıcı ve yumuşak dürbün yakınlaştırması sağlar.");
        en("mod.smooth_zoom.row1_desc", "Provides smooth binoculars zoom when key is pressed.");
        tr("mod.smooth_zoom.key_title", "Tuş & Varsayılan Zoom: ");
        en("mod.smooth_zoom.key_title", "Key & Default Zoom: ");
        tr("mod.smooth_zoom.key_desc", "İlk basıştaki yakınlaştırma çarpanı ve aktif kısayol tuşu.");
        en("mod.smooth_zoom.key_desc", "Initial zoom multiplier and active hotkey.");
        tr("mod.smooth_zoom.scroll_title", "Mouse Tekerleğiyle Zoom (Scroll)");
        en("mod.smooth_zoom.scroll_title", "Mouse Wheel Zoom (Scroll)");
        tr("mod.smooth_zoom.scroll_desc", "Zoom sırasında tekerlekle dinamik yakınlaştırıp uzaklaştırma (+/- 0.5x).");
        en("mod.smooth_zoom.scroll_desc", "Dynamically zoom in/out with scroll wheel during zoom (+/- 0.5x).");
        tr("mod.smooth_zoom.cinematic_title", "Sinematik Kamera (Atalet / Kayma)");
        en("mod.smooth_zoom.cinematic_title", "Cinematic Camera (Inertia)");
        tr("mod.smooth_zoom.cinematic_desc", "Atalet kamerası: fare durdurulsa bile ekran yumuşakça süzülerek durur.");
        en("mod.smooth_zoom.cinematic_desc", "Inertia camera: mouse glides smoothly with subtle momentum.");
        tr("mod.smooth_zoom.bars_title", "Sinematik Mod (Zoom)");
        en("mod.smooth_zoom.bars_title", "Cinematic Aspect Bars (Zoom)");
        tr("mod.smooth_zoom.bars_desc", "Zoom yapıldığında sinematik hava katan siyah film barları gösterir.");
        en("mod.smooth_zoom.bars_desc", "Displays cinematic black bars when zooming.");
        tr("mod.smooth_zoom.bars_always_title", "Sinematik Mod (Always)");
        en("mod.smooth_zoom.bars_always_title", "Cinematic Aspect Bars (Always)");
        tr("mod.smooth_zoom.bars_always_desc", "Zoom yapmasanız bile sinematik siyah barları sürekli açık tutar.");
        en("mod.smooth_zoom.bars_always_desc", "Keep cinematic black bars active at all times.");
        tr("mod.smooth_zoom.bar_size_title", "Sinematik Bar Boyutu");
        en("mod.smooth_zoom.bar_size_title", "Cinematic Bar Size");
        tr("mod.smooth_zoom.bar_size_desc", "Siyah film barlarının dikey kalınlığını ayarlar.");
        en("mod.smooth_zoom.bar_size_desc", "Adjusts the vertical height of black cinematic bars.");
        tr("mod.smooth_zoom.sens_title", "Hassasiyet Ölçekleme");
        en("mod.smooth_zoom.sens_title", "Sensitivity Scaling");
        tr("mod.smooth_zoom.sens_desc", "Zoom seviyesi arttıkça fare hassasiyetini orantılı sönümleyerek nişanı kolaylaştırır.");
        en("mod.smooth_zoom.sens_desc", "Reduces mouse sensitivity proportionally as zoom level increases.");
        tr("mod.smooth_zoom.speed_title", "Akıcılık Hızı & Sıfırla");
        en("mod.smooth_zoom.speed_title", "Smoothness Speed & Reset");
        tr("mod.smooth_zoom.speed_desc", "Animasyon süresi: Yavaş (Sinematik) / Doğal (Standart) / Hızlı.");
        en("mod.smooth_zoom.speed_desc", "Animation speed: Slow (Cinematic) / Normal (Standard) / Fast.");
        tr("mod.smooth_zoom.slow", "Yavaş");
        en("mod.smooth_zoom.slow", "Slow");
        tr("mod.smooth_zoom.normal", "Doğal");
        en("mod.smooth_zoom.normal", "Normal");
        tr("mod.smooth_zoom.fast", "Hızlı");
        en("mod.smooth_zoom.fast", "Fast");

        // Chat QoL
        tr("mod.chat_qol.title", "Chat QoL");
        en("mod.chat_qol.title", "Chat QoL");
        tr("mod.chat_qol.desc", "Sınırsız chat logu, Peek modu, mesaj birleştirici & panoya kopyalama.");
        en("mod.chat_qol.desc", "Unlimited chat history, Peek mode, message compacting & clipboard copy.");
        tr("mod.chat_qol.infinite_title", "Sınırsız Chat Logu");
        en("mod.chat_qol.infinite_title", "Unlimited Chat History");
        tr("mod.chat_qol.infinite_desc", "100 satır limitini kaldırır, sınırsız mesaj kaydırma sağlar.");
        en("mod.chat_qol.infinite_desc", "Removes 100-line limit and keeps infinite chat messages.");
        tr("mod.chat_qol.peek_title", "Chat Peek (İnceleme)");
        en("mod.chat_qol.peek_title", "Chat Peek");
        tr("mod.chat_qol.peek_desc", "Tuşa basılıyken hareket kesilmeden sohbet açılır ve tekerlekle taranır.");
        en("mod.chat_qol.peek_desc", "Hold key to scroll and inspect chat without stopping movement.");
        tr("mod.chat_qol.compact_title", "Mesaj Birleştirici");
        en("mod.chat_qol.compact_title", "Message Compacter");
        tr("mod.chat_qol.compact_desc", "Art arda gelen aynı mesajları tek satırda birleştirir: Mesaj (Nx).");
        en("mod.chat_qol.compact_desc", "Compact duplicate consecutive messages: Message (Nx).");
        tr("mod.chat_qol.copy_title", "Sohbet Kopyalama");
        en("mod.chat_qol.copy_title", "Chat Copying");
        tr("mod.chat_qol.copy_desc", "Sohbet açıkken mesaja Ctrl + Sol Tık ile tıklayarak panoya kopyalar.");
        en("mod.chat_qol.copy_desc", "Ctrl + Left Click on any message in chat to copy to clipboard.");

        // Tooltip QoL
        tr("mod.tooltip_qol.title", "Tooltip QoL");
        en("mod.tooltip_qol.title", "Tooltip QoL");
        tr("mod.tooltip_qol.desc", "Uzun eşya açıklamalarını mouse tekerleğiyle kaydırma & Ctrl ile dinamik zoom.");
        en("mod.tooltip_qol.desc", "Scroll lengthy tooltips with mouse wheel & dynamic Ctrl zoom.");
        tr("mod.tooltip_qol.scroll_title", "Tooltip Kaydırma (Scroll)");
        en("mod.tooltip_qol.scroll_title", "Tooltip Scrolling");
        tr("mod.tooltip_qol.scroll_desc", "Eşyanın üzerine gelindiğinde mouse tekerleğiyle açıklamayı yukarı/aşağı kaydırır.");
        en("mod.tooltip_qol.scroll_desc", "Scroll tooltip up and down using mouse wheel.");
        tr("mod.tooltip_qol.scale_title", "Ctrl ile Boyutlandırma (Zoom)");
        en("mod.tooltip_qol.scale_title", "Ctrl Zoom & Scaling");
        tr("mod.tooltip_qol.scale_desc", "Ctrl basılıyken tekerlek çevrildiğinde tooltip imleç etrafında dinamik ölçeklenir.");
        en("mod.tooltip_qol.scale_desc", "Dynamic zoom in/out around cursor using Ctrl + scroll.");
        tr("mod.tooltip_qol.current_scale", "Mevcut Tooltip Ölçeği: %");
        en("mod.tooltip_qol.current_scale", "Current Tooltip Scale: ");
        tr("mod.tooltip_qol.scale_reset_desc", "Ctrl + Tekerlek ile anlık büyütüp küçültebilir veya butondan sıfırlayabilirsiniz.");
        en("mod.tooltip_qol.scale_reset_desc", "Scale with Ctrl + Scroll or reset to default using button.");
        tr("mod.tooltip_qol.reset_100", "%100 Sıfırla");
        en("mod.tooltip_qol.reset_100", "Reset (100%)");

        // Anti-AFK
        tr("mod.anti_afk.title", "Anti-AFK Modülü");
        en("mod.anti_afk.title", "Anti-AFK Module");
        tr("mod.anti_afk.desc", "Otomatik aralıklarla (1-10 dk) Shift, Jump veya Click ile AFK düşmeyi önler.");
        en("mod.anti_afk.desc", "Prevents AFK disconnects at chosen intervals via Shift, Jump or Click.");
        tr("mod.anti_afk.row1_title", "Anti-AFK Modülü");
        en("mod.anti_afk.row1_title", "Anti-AFK Module");
        tr("mod.anti_afk.row1_desc", "Açık olduğunda belirlenen aralıklarla seçilen eylemi otomatik uygular.");
        en("mod.anti_afk.row1_desc", "Automatically triggers selected action at chosen time intervals.");
        tr("mod.anti_afk.interval_title", "Çalışma Aralığı");
        en("mod.anti_afk.interval_title", "Interval Time");
        tr("mod.anti_afk.interval_desc", "Eylemin kaç dakikada bir tekrarlanacağını seçin.");
        en("mod.anti_afk.interval_desc", "Choose how often the action will be performed.");
        tr("mod.anti_afk.min", "Dk");
        en("mod.anti_afk.min", "Min");
        tr("mod.anti_afk.action_title", "Gerçekleştirilecek Eylem");
        en("mod.anti_afk.action_title", "Action to Perform");
        tr("mod.anti_afk.action_desc", "Süre dolduğunda karakterin otomatik yapacağı eylem.");
        en("mod.anti_afk.action_desc", "The action triggered when timer elapses.");
        tr("mod.anti_afk.status_title", "Durum & Varsayılana Dön");
        en("mod.anti_afk.status_title", "Status & Reset Defaults");

        // AFK Cinematics
        tr("mod.afk_cinematics.title", "AFK Sinematik Kamera");
        en("mod.afk_cinematics.title", "AFK Cinematic Camera");
        tr("mod.afk_cinematics.desc", "Karakteri farklı açılardan gösteren çok sahneli dinamik sinematik kamera.");
        en("mod.afk_cinematics.desc", "Multi-shot dynamic cinematic camera displaying character from various angles.");
        tr("mod.afk_cinematics.row1_title", "AFK Sinematik Modülü");
        en("mod.afk_cinematics.row1_title", "AFK Cinematic Module");
        tr("mod.afk_cinematics.row1_desc", "Hareketsiz kalındığında dinamik çok açılı kamera geçişleri başlatır.");
        en("mod.afk_cinematics.row1_desc", "Triggers multi-shot dynamic cinematic cameras when character is idle.");
        tr("mod.afk_cinematics.time_title", "Hareketsizlik Süresi");
        en("mod.afk_cinematics.time_title", "Idle Timeout");
        tr("mod.afk_cinematics.time_desc", "Kameranın devreye girmesi için gereken hareketsizlik süresi.");
        en("mod.afk_cinematics.time_desc", "Inactivity time required to trigger the cinematic camera.");
        tr("mod.afk_cinematics.bars_title", "Sinematik Siyah Barlar");
        en("mod.afk_cinematics.bars_title", "Cinematic Aspect Bars");
        tr("mod.afk_cinematics.bars_desc", "Kamera geçişlerinde üst ve altta siyah film barları gösterir.");
        en("mod.afk_cinematics.bars_desc", "Displays black letterbox bars during cinematic camera.");
        tr("mod.afk_cinematics.raycast_title", "Duvar Çarpışma Kontrolü (Raycast)");
        en("mod.afk_cinematics.raycast_title", "Wall Collision Raycast");
        tr("mod.afk_cinematics.raycast_desc", "Kameranın duvarların ve blokların içine girmesini engeller.");
        en("mod.afk_cinematics.raycast_desc", "Prevents camera from clipping into walls and blocks.");
        tr("mod.afk_cinematics.test_title", "Önizleme & Test");
        en("mod.afk_cinematics.test_title", "Instant Preview & Test");
        tr("mod.afk_cinematics.test_desc", "Menüyü kapatıp kamerayı hemen 3 saniye içinde test eder.");
        en("mod.afk_cinematics.test_desc", "Closes the menu and triggers camera test within 3 seconds.");
        tr("mod.afk_cinematics.test_btn", "🎬 Şimdi Test Et");
        en("mod.afk_cinematics.test_btn", "🎬 Test Camera Now");

        // Cosmetics (Capes)
        tr("mod.cosmetics.title", "Pelerin Yöneticisi (Capes)");
        en("mod.cosmetics.title", "Cape Manager (Capes)");
        tr("mod.cosmetics.desc", "Sanara, Rikka ve kullanıcı tanımlı GIF/PNG pelerinler.");
        en("mod.cosmetics.desc", "Sanara, Rikka, and user custom GIF/PNG capes.");

        tr("cosmetics.custom_title", "ÖZEL PELERİN");
        en("cosmetics.custom_title", "CUSTOM CAPE");
        tr("cosmetics.open_folder", "📂 Klasör");
        en("cosmetics.open_folder", "📂 Folder");
        tr("cosmetics.empty_folder", "📂 Klasör Boş");
        en("cosmetics.empty_folder", "📂 Folder Empty");
        tr("cosmetics.empty_sub", "kiem/capes içine");
        en("cosmetics.empty_sub", "into kiem/capes");
        tr("cosmetics.empty_hint", ".gif veya .png atın");
        en("cosmetics.empty_hint", "drop .gif or .png");
        tr("cosmetics.open_folder_btn", "➕ Klasörü Aç");
        en("cosmetics.open_folder_btn", "➕ Open Folder");
        tr("cosmetics.equip", "KUŞAN");
        en("cosmetics.equip", "EQUIP");
        tr("cosmetics.equipped", "✓ KUŞANILDI");
        en("cosmetics.equipped", "✓ EQUIPPED");
        tr("cosmetics.true_color_on", "Canlı Renk: AÇIK");
        en("cosmetics.true_color_on", "True Color: ON");
        tr("cosmetics.true_color_off", "Canlı Renk: KAPALI");
        en("cosmetics.true_color_off", "True Color: OFF");

        tr("cosmetics.unequip", "✕ Çıkar");
        en("cosmetics.unequip", "✕ Unequip");
        tr("cosmetics.other_active", "● Başka Pelerin Aktif");
        en("cosmetics.other_active", "● Another Cape Active");
        tr("cosmetics.inactive", "○ Pasif");
        en("cosmetics.inactive", "○ Inactive");

        tr("cosmetics.active_selection", "Aktif Seçim");
        en("cosmetics.active_selection", "Active Selection");
        tr("cosmetics.none", "Pelerin Seçilmedi (Pasif)");
        en("cosmetics.none", "No Cape Equipped (Inactive)");
        tr("cosmetics.custom_cape_prefix", "Özel Pelerin");
        en("cosmetics.custom_cape_prefix", "Custom Cape");
        tr("cosmetics.info_desc", "Seçilen pelerin anında uygulanır ve kaydedilir.");
        en("cosmetics.info_desc", "Selected cape is applied and saved instantly.");
    }

    private static void tr(String key, String value) {
        TR_MAP.put(key, value);
    }

    private static void en(String key, String value) {
        EN_MAP.put(key, value);
    }

    public static String get(String key) {
        Language current = Language.fromCode(KiemConfig.language);
        if (current == Language.EN) {
            String val = EN_MAP.get(key);
            if (val != null) return val;
        }
        String val = TR_MAP.get(key);
        return val != null ? val : key;
    }

    public static String get(String key, String fallback) {
        String val = get(key);
        return (val != null && !val.equals(key)) ? val : fallback;
    }
}
