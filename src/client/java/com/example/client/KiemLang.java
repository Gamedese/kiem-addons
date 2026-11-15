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
        tr("welcome.nav_cosmetics", "✨ Gardırobu Aç");
        en("welcome.nav_cosmetics", "✨ Open Wardrobe");
        tr("welcome.latest", "Güncel");
        en("welcome.latest", "Latest");
        tr("welcome.update_prefix", "✦ Güncelle");
        en("welcome.update_prefix", "✦ Update");
        tr("welcome.keybind_prefix", "Kısayol:");
        en("welcome.keybind_prefix", "Keybind:");
        tr("welcome.links_title", "Topluluk & Bağlantılar");
        en("welcome.links_title", "Community & Links");
        tr("welcome.whats_new", "✨ Yenilikler — v1.1");
        en("welcome.whats_new", "✨ What's New — v1.1");
        tr("welcome.whats_new.1", "✿ Sakura & Ink Boutique Arayüz, Temalar & Maskot");
        en("welcome.whats_new.1", "✿ Sakura & Ink Boutique UI, Themes & Mascot");
        tr("welcome.whats_new.2", "⚔ Yeni Blockbench Şapka, Maske & Bel Kozmetikleri");
        en("welcome.whats_new.2", "⚔ Redesigned Blockbench Hats, Masks & Waist");
        tr("welcome.whats_new.3", "⚙ Gardırop Customize (Y/Z/Ölçek) & Gardırop Sıfırlama");
        en("welcome.whats_new.3", "⚙ Wardrobe Customize (Y/Z/Scale) & Reset");
        tr("welcome.whats_new.4", "⚡ 256-Renk Pelerin Optimizasyonu & Güncelleme Kontrolü");
        en("welcome.whats_new.4", "⚡ 256-Color Cape Optimization & Update Checker");
        tr("welcome.edit_arrow", "Düzenle →");
        en("welcome.edit_arrow", "Edit →");
        tr("wardrobe.reset_btn", "Gardırop Sıfırla");
        en("wardrobe.reset_btn", "Reset Wardrobe");

        // --- RESET MODAL ---
        tr("reset_modal.title", "⚠ Tüm Ayarları Sıfırla");
        en("reset_modal.title", "⚠ Reset All Settings");
        tr("reset_modal.desc1", "Tüm Kiem Addons ayarlarını varsayılana");
        en("reset_modal.desc1", "Are you sure you want to restore all Kiem Addons");
        tr("reset_modal.desc2", "döndürmek istediğinize emin misiniz?");
        en("reset_modal.desc2", "settings to their default values?");
        tr("reset_modal.desc3", "Bu işlem geri alınamaz.");
        en("reset_modal.desc3", "This action cannot be undone.");
        tr("reset_wardrobe_modal.title", "⚠ Gardırobu Sıfırla");
        en("reset_wardrobe_modal.title", "⚠ Reset Wardrobe");
        tr("reset_wardrobe_modal.desc1", "Gardırop ayarlarını sıfırlamak istediğine emin misin?");
        en("reset_wardrobe_modal.desc1", "Are you sure you want to reset your wardrobe?");
        tr("reset_wardrobe_modal.desc2", "Pelerin, kanat, şapka, maske, el, bel ve ayak çıkarılır");
        en("reset_wardrobe_modal.desc2", "Unequips cape, wings, hats, masks, hand, waist & feet");
        tr("reset_wardrobe_modal.desc3", "ve Customize konumları temizlenir (Pet & Trail korunur).");
        en("reset_wardrobe_modal.desc3", "and clears Customize offsets (keeps Pets & Trails).");

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

        // Cosmetics Sub-tabs
        tr("cosmetics.sub.capes", "Pelerinler");
        en("cosmetics.sub.capes", "Capes");
        tr("cosmetics.sub.auras", "Auralar");
        en("cosmetics.sub.auras", "Auras");
        tr("cosmetics.sub.trails", "İzler (Trails)");
        en("cosmetics.sub.trails", "Trails");
        tr("trails.sakura.title", "Sakura Bloom İzi");
        en("trails.sakura.title", "Sakura Bloom Trail");
        tr("auras.sakura.title", "Sakura Lop");
        en("auras.sakura.title", "Sakura Lop");
        tr("auras.sakura.subtitle", "Küçük bir bahar yörüngesi");
        en("auras.sakura.subtitle", "A little orbit of spring");
        tr("auras.sakura.desc1", "Üç minik tavşan, süzülen bir yörünge.");
        en("auras.sakura.desc1", "Three tiny bunnies in a bobbing orbit.");
        tr("auras.sakura.desc2", "Krem ve pembe, kısa sevimli kulaklar.");
        en("auras.sakura.desc2", "Cream and pink, with cute short ears.");
        tr("auras.sakura.desc3", "Arkalarında süzülen sakura yaprakları.");
        en("auras.sakura.desc3", "Drifting sakura petals follow behind.");
        tr("auras.local", "Yalnızca sana görünür.");
        en("auras.local", "Only visible to you.");
        tr("auras.compatible", "Pet ve pelerinle birlikte kullanılabilir.");
        en("auras.compatible", "Works alongside your pet and cape.");
        tr("auras.enable", "Aurayı aç");
        en("auras.enable", "Enable aura");
        tr("auras.disable", "Aurayı kapat");
        en("auras.disable", "Disable aura");
        tr("auras.custom.title", "Sakura Lop • Özelleştir");
        en("auras.custom.title", "Sakura Lop • Customize");
        tr("auras.custom.hint", "Canlı ayarlar • 1x hız = 4 saniyelik tur");
        en("auras.custom.hint", "Live settings • 1x speed = a 4-second orbit");
        tr("auras.custom.scale", "Boyut");
        en("auras.custom.scale", "Scale");
        tr("auras.custom.speed", "Hız");
        en("auras.custom.speed", "Speed");
        tr("auras.custom.bob", "Salınım");
        en("auras.custom.bob", "Bobbing");
        tr("auras.custom.height", "Yükseklik");
        en("auras.custom.height", "Height");
        tr("auras.custom.radius", "Uzaklık");
        en("auras.custom.radius", "Radius");
        tr("auras.custom.blocks", "blok");
        en("auras.custom.blocks", "blocks");
        tr("auras.custom.bunny", "Tavşan");
        en("auras.custom.bunny", "Bunny");
        tr("auras.custom.petals", "Havadaki Yapraklar");
        en("auras.custom.petals", "Floating Petals");
        tr("auras.custom.ground_bloom", "🌸 Çiçek Halısı");
        en("auras.custom.ground_bloom", "🌸 Flower Carpet");
        tr("auras.custom.bloom.0", "Kapalı");
        en("auras.custom.bloom.0", "Off");
        tr("auras.custom.bloom.1", "Tek Tük");
        en("auras.custom.bloom.1", "Sparse");
        tr("auras.custom.bloom.2", "Normal");
        en("auras.custom.bloom.2", "Normal");
        tr("auras.custom.bloom.3", "Dolu Çember");
        en("auras.custom.bloom.3", "Full Ring");
        tr("auras.custom.density.0", "Kapalı");
        en("auras.custom.density.0", "Off");
        tr("auras.custom.density.1", "Az");
        en("auras.custom.density.1", "Low");
        tr("auras.custom.density.2", "Normal");
        en("auras.custom.density.2", "Normal");
        tr("auras.custom.density.3", "Yoğun");
        en("auras.custom.density.3", "High");
        tr("auras.custom.defaults", "Aura ayarlarını sıfırla");
        en("auras.custom.defaults", "Reset aura settings");
        tr("auras.custom.reset_setting", "Bu ayarı varsayılana döndür");
        en("auras.custom.reset_setting", "Reset this setting to its default");
        tr("auras.custom.speed_hint", "1x = 4 saniye/tur. Hız değişirken yörünge kesilmez.");
        en("auras.custom.speed_hint", "1x = 4 seconds/orbit. Changing speed preserves the orbit phase.");
        tr("auras.custom.petals_hint", "Havadaki uçuşan yaprak yoğunluğu: Kapalı → Az → Normal → Yoğun");
        en("auras.custom.petals_hint", "Floating petal density: Off → Low → Normal → High");
        tr("auras.custom.bloom_hint", "Zemin çiçek halısı: Kapalı → Tek Tük → Normal → Dolu Çember");
        en("auras.custom.bloom_hint", "Ground flower carpet: Off → Sparse → Normal → Full Ring");
        tr("auras.custom.general", "Genel");
        en("auras.custom.general", "General");
        tr("auras.custom.preview", "Önizleme");

        // Pet Customization Screen
        tr("pets.custom.btn", "Özelleştir");
        en("pets.custom.btn", "Customize");
        tr("pets.custom.title", "Yoldaş Özelleştirme");
        en("pets.custom.title", "Companion Customization");
        tr("pets.custom.ground_roam", "Yerde Gezme");
        en("pets.custom.ground_roam", "Ground Roam");
        tr("pets.custom.mode", "Mod");
        en("pets.custom.mode", "Mode");
        tr("pets.custom.mode.auto", "Otomatik (Omuz + Yer)");
        en("pets.custom.mode.auto", "Auto (Shoulder + Ground)");
        tr("pets.custom.mode.shoulder", "Sadece Omuz");
        en("pets.custom.mode.shoulder", "Shoulder Only");
        tr("pets.custom.mode.ground", "Tam Yerde (Takip)");
        en("pets.custom.mode.ground", "Full Ground (Follow)");
        tr("pets.custom.bored_time", "Omuzdan İnme Süresi");
        en("pets.custom.bored_time", "Hop-Down Delay");
        tr("pets.custom.scale", "Pet Boyutu");
        en("pets.custom.scale", "Pet Scale");
        tr("pets.custom.eat_seed", "Yemek Paylaşma (Çekirdek)");
        en("pets.custom.eat_seed", "Snack Time (Seed)");
        tr("pets.custom.dry_shake", "Sudan Çıkınca Silkinme");
        en("pets.custom.dry_shake", "Wet Shake-Off");
        tr("pets.custom.sleep", "Sabit Kalınca Uyuklama");
        en("pets.custom.sleep", "Idle Sleep Mode");
        tr("pets.custom.octo_mood", "Ruh Hali");
        en("pets.custom.octo_mood", "Mood Lock");
        tr("pets.custom.octo_mood.auto", "Otomatik (Can/Karanlık/Ateş)");
        en("pets.custom.octo_mood.auto", "Auto (HP/Dark/Fire)");
        tr("pets.custom.octo_mood.happy", "Hep Mutlu (Pembe)");
        en("pets.custom.octo_mood.happy", "Always Happy (Pink)");
        tr("pets.custom.octo_mood.angry", "Hep Kızgın (Mavi)");
        en("pets.custom.octo_mood.angry", "Always Angry (Blue)");
        tr("pets.custom.octo_reactive", "Reaktif Animasyonlar");
        en("pets.custom.octo_reactive", "Reactive Animations");
        tr("pets.custom.defaults", "Varsayılanlara Sıfırla");
        en("pets.custom.defaults", "Reset Defaults");
        tr("pets.custom.on", "Açık");
        en("pets.custom.on", "On");
        tr("pets.custom.off", "Kapalı");
        en("pets.custom.off", "Off");
        tr("pets.custom.roam_note", "Sabit durduğunda pet omuzdan atlayıp etrafında gezinir.");
        en("pets.custom.roam_note", "Hops off your shoulder when you stand still and follows you.");
        tr("pets.custom.state_shoulder", "Şu an: Omuzda");
        en("pets.custom.state_shoulder", "Status: On Shoulder");
        tr("pets.custom.state_ground", "Şu an: Yerde Geziyor");
        en("pets.custom.state_ground", "Status: Roaming Ground");
        en("auras.custom.preview", "Preview");
        tr("auras.custom.live", "CANLI");
        en("auras.custom.live", "LIVE");
        tr("auras.custom.drag_hint", "Çevirmek için sürükle");
        en("auras.custom.drag_hint", "Drag to rotate");
        tr("auras.custom.reset_view", "Görünümü sıfırla");
        en("auras.custom.reset_view", "Reset view");

        // --- OBSIDIAN VOID GARGOYLE AURA ---
        tr("auras.void_gargoyle.title", "Obsidyen Hiçlik Heykeli");
        en("auras.void_gargoyle.title", "Obsidian Void Gargoyle");
        tr("auras.void_gargoyle.subtitle", "Karanlık Obsidyen & Akkor Magma Aurası");
        en("auras.void_gargoyle.subtitle", "Dark Obsidian & Molten Magma Aura");
        tr("auras.void_gargoyle.desc1", "Etrafında süzülen 3 obsidyen yavru gargoyle");
        en("auras.void_gargoyle.desc1", "3 orbiting obsidian imp whelps");
        tr("auras.void_gargoyle.desc2", "Yırtıcı çene açılma refleksi & lav közleri");
        en("auras.void_gargoyle.desc2", "Predatory jaw snapping & lava ember bursts");
        tr("auras.void_gargoyle.desc3", "Bastığın adımlardan tüten kara duman ve korlar");
        en("auras.void_gargoyle.desc3", "Rising dark smoke and ground ember trail");

        // Gargoyle Aliases for title & description
        tr("auras.gargoyle.title", "Obsidyen Hiçlik Heykeli");
        en("auras.gargoyle.title", "Obsidian Void Gargoyle");
        tr("auras.gargoyle.subtitle", "Karanlık Obsidyen & Akkor Magma Aurası");
        en("auras.gargoyle.subtitle", "Dark Obsidian & Molten Magma Aura");
        tr("auras.gargoyle.desc1", "Etrafında süzülen 3 obsidyen yavru gargoyle");
        en("auras.gargoyle.desc1", "3 orbiting obsidian imp whelps");
        tr("auras.gargoyle.desc2", "Yırtıcı çene açılma refleksi & lav közleri");
        en("auras.gargoyle.desc2", "Predatory jaw snapping & lava ember bursts");
        tr("auras.gargoyle.desc3", "Bastığın adımlardan tüten kara duman ve korlar");
        en("auras.gargoyle.desc3", "Rising dark smoke and ground ember trail");

        // Gargoyle Settings & Levels
        tr("auras.gargoyle.embers", "Akkor Közler");
        en("auras.gargoyle.embers", "Lava Embers");
        tr("auras.gargoyle.ground_smoke", "Zemin Dumanı");
        en("auras.gargoyle.ground_smoke", "Ground Smoke");
        tr("auras.gargoyle.jaw_rate", "Çene Sıklığı");
        en("auras.gargoyle.jaw_rate", "Jaw Snapping");

        tr("auras.gargoyle.off", "Kapalı");
        en("auras.gargoyle.off", "Off");
        tr("auras.gargoyle.low", "Hafif");
        en("auras.gargoyle.low", "Low");
        tr("auras.gargoyle.normal", "Normal");
        en("auras.gargoyle.normal", "Normal");
        tr("auras.gargoyle.high", "Yoğun");
        en("auras.gargoyle.high", "High");

        tr("auras.gargoyle.jaw.slow", "Sakin");
        en("auras.gargoyle.jaw.slow", "Calm");
        tr("auras.gargoyle.jaw.normal", "Normal");
        en("auras.gargoyle.jaw.normal", "Normal");
        tr("auras.gargoyle.jaw.aggressive", "Yırtıcı");
        en("auras.gargoyle.jaw.aggressive", "Aggressive");

        // Gargoyle Customize Screen helpers
        tr("auras.gargoyle.custom.title", "Obsidyen Hiçlik Heykeli • Özelleştir");
        en("auras.gargoyle.custom.title", "Obsidian Void Gargoyle • Customize");
        tr("auras.void_gargoyle.custom.title", "Obsidyen Hiçlik Heykeli • Özelleştir");
        en("auras.void_gargoyle.custom.title", "Obsidian Void Gargoyle • Customize");
        tr("auras.gargoyle.whelp", "Gargoyle");
        en("auras.gargoyle.whelp", "Gargoyle");
        tr("auras.gargoyle.embers_hint", "Havada patlayan lav közlerinin yoğunluğu");
        en("auras.gargoyle.embers_hint", "Density of floating lava embers");
        tr("auras.gargoyle.smoke_hint", "Zeminden yükselen volkanik kara duman yoğunluğu");
        en("auras.gargoyle.smoke_hint", "Density of rising ground dark smoke");
        tr("auras.gargoyle.jaw_hint", "Yavru gargoyleların çene açıp kapama refleksi");
        en("auras.gargoyle.jaw_hint", "Jaw snapping rate of imp whelps");

        // Numeric indexed level aliases
        tr("auras.gargoyle.embers.0", "Kapalı");
        en("auras.gargoyle.embers.0", "Off");
        tr("auras.gargoyle.embers.1", "Hafif");
        en("auras.gargoyle.embers.1", "Low");
        tr("auras.gargoyle.embers.2", "Normal");
        en("auras.gargoyle.embers.2", "Normal");
        tr("auras.gargoyle.embers.3", "Yoğun");
        en("auras.gargoyle.embers.3", "High");

        tr("auras.gargoyle.smoke.0", "Kapalı");
        en("auras.gargoyle.smoke.0", "Off");
        tr("auras.gargoyle.smoke.1", "Hafif");
        en("auras.gargoyle.smoke.1", "Low");
        tr("auras.gargoyle.smoke.2", "Normal");
        en("auras.gargoyle.smoke.2", "Normal");
        tr("auras.gargoyle.smoke.3", "Yoğun");
        en("auras.gargoyle.smoke.3", "High");

        tr("auras.gargoyle.ground_smoke.0", "Kapalı");
        en("auras.gargoyle.ground_smoke.0", "Off");
        tr("auras.gargoyle.ground_smoke.1", "Hafif");
        en("auras.gargoyle.ground_smoke.1", "Low");
        tr("auras.gargoyle.ground_smoke.2", "Normal");
        en("auras.gargoyle.ground_smoke.2", "Normal");
        tr("auras.gargoyle.ground_smoke.3", "Yoğun");
        en("auras.gargoyle.ground_smoke.3", "High");

        tr("auras.gargoyle.jaw.0", "Sakin");
        en("auras.gargoyle.jaw.0", "Calm");
        tr("auras.gargoyle.jaw.1", "Normal");
        en("auras.gargoyle.jaw.1", "Normal");
        tr("auras.gargoyle.jaw.2", "Yırtıcı");
        en("auras.gargoyle.jaw.2", "Aggressive");

        tr("auras.gargoyle.jaw_rate.0", "Sakin");
        en("auras.gargoyle.jaw_rate.0", "Calm");
        tr("auras.gargoyle.jaw_rate.1", "Normal");
        en("auras.gargoyle.jaw_rate.1", "Normal");
        tr("auras.gargoyle.jaw_rate.2", "Yırtıcı");
        en("auras.gargoyle.jaw_rate.2", "Aggressive");
        tr("cosmetics.sub.pets", "Omuz Yoldaşları");
        en("cosmetics.sub.pets", "Shoulder Pets");

        // Shoulder Pets
        tr("pets.none", "Yoldaş Yok (Pasif)");
        en("pets.none", "No Pet Equipped (Inactive)");
        tr("pets.hamster.title", "Klasik Hamster");
        en("pets.hamster.title", "Classic Hamster");
        tr("pets.hamster.desc", "Tombul patileri ve minik burnuyla omzunuzda nefes alan sevimli dost.");
        en("pets.hamster.desc", "Chubby paws and breathing animation on your shoulder.");
        tr("pets.hamster_hd.title", "Pofuduk Hamster");
        en("pets.hamster_hd.title", "Fluffy Hamster");
        tr("pets.hamster_hd.desc", "Pofuduk tombul yanakları, pembe patileri ve ışıl ışıl gözleriyle HD dostumuz.");
        en("pets.hamster_hd.desc", "Ultra-chubby cheeks, pink paws and sparkling eyes in high definition.");
        tr("pets.octopus.title", "Ruh Hali Ahtapotu");
        en("pets.octopus.title", "Mood Octopus");
        tr("pets.octopus.desc", "Mutlu pembeden kızgın maviye takla atan peluş ahtapot.");
        en("pets.octopus.desc", "Flipping plush octopus changing between happy and angry mood.");
        tr("pets.dragon.title", "Bebek Ender Ejderhası");
        en("pets.dragon.title", "Baby Ender Dragon");
        tr("pets.dragon.desc", "Mor parlayan gözleri, minik kanatları ve kıvrık kuyruğu olan bebek ejderha.");
        en("pets.dragon.desc", "Cute baby dragon with glowing purple eyes, flapping wings and tail.");
        tr("pets.void_gargoyle.title", "Obsidyen Gargoyle");
        en("pets.void_gargoyle.title", "Obsidian Gargoyle");
        tr("pets.void_gargoyle.desc", "Omuzda tüneyen, akkor lav boğazlı ve parlayan kızıl gözlü minik obsidyen ejderha.");
        en("pets.void_gargoyle.desc", "Shoulder-perched obsidian dragon with glowing red eyes and molten gullet.");
        tr("pets.dragon.variant_ender", "Ejderha: Ender");
        en("pets.dragon.variant_ender", "Dragon: Ender");
        tr("pets.dragon.variant_toothless", "Ejderha: Dişsiz (Toothless)");
        en("pets.dragon.variant_toothless", "Dragon: Toothless");
        tr("pets.inventory_curse.title", "Lanet Solucanı (Toji)");
        en("pets.inventory_curse.title", "Inventory Curse (Toji)");
        tr("pets.inventory_curse.desc", "Toji Fushiguro'nun gövdeye dolanan ve ağzında silah taşıyan lanet ruhu.");
        en("pets.inventory_curse.desc", "Toji Fushiguro's coiled torso curse spirit holding the Inverted Spear.");
        tr("pets.worm.sword_on", "⚔ Kılıç: AÇIK");
        en("pets.worm.sword_on", "⚔ Sword: ON");
        tr("pets.worm.sword_off", "⚔ Kılıç: KAPALI");
        en("pets.worm.sword_off", "⚔ Sword: OFF");
        tr("pets.customize.btn", "⚙ Özelleştir");
        en("pets.customize.btn", "⚙ Customize");

        tr("pets.side.title", "Omuz Seçimi");
        en("pets.side.title", "Shoulder Side");
        tr("pets.side.right", "Sağ Omuz");
        en("pets.side.right", "Right Shoulder");
        tr("pets.side.left", "Sol Omuz");
        en("pets.side.left", "Left Shoulder");
        tr("pets.active_selection", "Aktif Yoldaş");
        en("pets.active_selection", "Active Companion");
        tr("pets.info_desc", "Seçilen yoldaş omzunuza konar, eğilme ve koşma hareketlerinize uyum sağlar.");
        en("pets.info_desc", "Selected companion sits on your shoulder and follows your movement.");

        // General Cosmetics Sub-Tabs
        tr("cosmetics.sub.wings", "Kanatlar");
        en("cosmetics.sub.wings", "Wings");
        tr("cosmetics.sub.hats", "Şapkalar");
        en("cosmetics.sub.hats", "Hats");
        tr("cosmetics.sub.neck", "Boyun");
        en("cosmetics.sub.neck", "Neck");
        tr("cosmetics.sub.hand", "El");
        en("cosmetics.sub.hand", "Hand");
        tr("cosmetics.sub.waist", "Bel");
        en("cosmetics.sub.waist", "Waist");
        tr("cosmetics.sub.foot", "Ayak");
        en("cosmetics.sub.foot", "Foot");
        tr("cosmetics.sub.trail", "İz Efektleri");
        en("cosmetics.sub.trail", "Trail Effects");

        tr("cosmetics.slot.active", "Aktif Seçim");
        en("cosmetics.slot.active", "Active Selection");
        tr("cosmetics.slot.none", "Takılı Değil");
        en("cosmetics.slot.none", "None Equipped");
        tr("cosmetics.slot.hint", "Tüm kozmetik slotları bağımsızdır ve aynı anda kombinlenebilir.");
        en("cosmetics.slot.hint", "All cosmetic slots are independent and can be combined freely.");
        tr("cosmetics.hand.first_person", "1. Şahıs Elde Göster");
        en("cosmetics.hand.first_person", "Show in First-Person");
        tr("cosmetics.preview.title", "3D Önizleme");
        en("cosmetics.preview.title", "3D Preview");

        // Wings
        tr("cosmetic.wing.seraphim.title", "Seraphim Kanatları");
        en("cosmetic.wing.seraphim.title", "Seraphim Wings");
        tr("cosmetic.wing.seraphim.desc", "Çift katmanlı simetrik beyaz melek kanatları.");
        en("cosmetic.wing.seraphim.desc", "Symmetrical double-tiered white angel wings.");

        tr("cosmetic.wing.valkyrie.title", "Valkür Kanatları");
        en("cosmetic.wing.valkyrie.title", "Valkyrie Wings");
        tr("cosmetic.wing.valkyrie.desc", "Altın işlemeli görkemli zırhlı melek kanatları.");
        en("cosmetic.wing.valkyrie.desc", "Majestic armored angel wings with golden filigree.");
        tr("cosmetic.wing.demon_dragon.title", "İblis Ejderha Kanatları");
        en("cosmetic.wing.demon_dragon.title", "Demon Dragon Wings");
        tr("cosmetic.wing.demon_dragon.desc", "Karanlık zarlı ve sivri pençeli ejderha kanatları.");
        en("cosmetic.wing.demon_dragon.desc", "Dark webbed dragon wings with sharp talons.");
        tr("cosmetic.wing.arcane_butterfly.title", "Büyülü Kelebek Kanatları");
        en("cosmetic.wing.arcane_butterfly.title", "Arcane Butterfly Wings");
        tr("cosmetic.wing.arcane_butterfly.desc", "Yarı saydam parlayan mistik kelebek kanatları.");
        en("cosmetic.wing.arcane_butterfly.desc", "Translucent glowing mystical butterfly wings.");
        tr("cosmetic.wing.fallen_angel.title", "Düşmüş Melek Kanatları");
        en("cosmetic.wing.fallen_angel.title", "Fallen Angel Wings");
        tr("cosmetic.wing.fallen_angel.desc", "Kül ve kuzgun siyahı tüylerle örülü kanatlar.");
        en("cosmetic.wing.fallen_angel.desc", "Ash and raven-black feathered wings.");
        tr("cosmetic.wing.vampire_bat.title", "Vampir Yarasa Kanatları");
        en("cosmetic.wing.vampire_bat.title", "Vampire Bat Wings");
        tr("cosmetic.wing.vampire_bat.desc", "Kızıl damarlı gece yarasa kanatları.");
        en("cosmetic.wing.vampire_bat.desc", "Crimson-veined nocturnal bat wings.");
        tr("cosmetic.wing.ender_dragon.title", "Ender Ejderha Kanatları");
        en("cosmetic.wing.ender_dragon.title", "Ender Dragon Wings");
        tr("cosmetic.wing.ender_dragon.desc", "Mor enerjili kadim End ejderhası kanatları.");
        en("cosmetic.wing.ender_dragon.desc", "Violet-infused ancient End dragon wings.");

        // Hats
        tr("cosmetic.hat.ronin.title", "Ronin Şapkası");
        en("cosmetic.hat.ronin.title", "Ronin Hat");
        tr("cosmetic.hat.ronin.desc", "Geleneksel hasır kasa şapka.");
        en("cosmetic.hat.ronin.desc", "Traditional wide-brimmed woven kasa hat.");
        tr("cosmetic.hat.shinobi_headband.title", "Shinobi Alın Bandı");
        en("cosmetic.hat.shinobi_headband.title", "Shinobi Headband");
        tr("cosmetic.hat.shinobi_headband.desc", "Metal plakalı ve dalgalanan kuyruklu ninja bandı.");
        en("cosmetic.hat.shinobi_headband.desc", "Ninja headband with metal plate and flowing tails.");
        tr("cosmetic.hat.witch.title", "Cadı Şapkası");
        en("cosmetic.hat.witch.title", "Witch Hat");
        tr("cosmetic.hat.witch.desc", "Sivri uçlu klasik büyücü şapkası.");
        en("cosmetic.hat.witch.desc", "Classic pointed spellcaster hat.");
        tr("cosmetic.hat.kitsune_ears.title", "Kitsune Kulakları");
        en("cosmetic.hat.kitsune_ears.title", "Kitsune Ears");
        tr("cosmetic.hat.kitsune_ears.desc", "Tepki veren tüylü dokuz kuyruk tilki kulakları.");
        en("cosmetic.hat.kitsune_ears.desc", "Expressive fluffy fox spirit ears.");
        tr("cosmetic.hat.celestial_halo.title", "Göksel Hale");
        en("cosmetic.hat.celestial_halo.title", "Celestial Halo");
        tr("cosmetic.hat.celestial_halo.desc", "Başın üzerinde süzülen kutsal altın ışık halkası.");
        en("cosmetic.hat.celestial_halo.desc", "Sacred golden light ring floating above the head.");
        tr("cosmetic.hat.oni_horns.title", "Oni Boynuzları");
        en("cosmetic.hat.oni_horns.title", "Oni Horns");
        tr("cosmetic.hat.oni_horns.desc", "Kıvrımlı kızıl iblis savaşçı boynuzları.");
        en("cosmetic.hat.oni_horns.desc", "Curved crimson demon warrior horns.");
        tr("cosmetic.hat.kitsune_mask.title", "Kitsune Maskesi");
        en("cosmetic.hat.kitsune_mask.title", "Kitsune Mask");
        tr("cosmetic.hat.kitsune_mask.desc", "Alında yana takılan geleneksel tilki festival maskesi.");
        en("cosmetic.hat.kitsune_mask.desc", "Traditional fox spirit mask worn tilted on the brow.");
        tr("cosmetic.hat.broken_halo.title", "Kırık Hale");
        en("cosmetic.hat.broken_halo.title", "Broken Halo");
        tr("cosmetic.hat.broken_halo.desc", "Çatlamış ve eğilmiş sönük kutsal hale.");
        en("cosmetic.hat.broken_halo.desc", "Shattered and tilted dim sacred halo.");
        tr("cosmetic.hat.royal_crown.title", "Taç");
        en("cosmetic.hat.royal_crown.title", "Crown");
        tr("cosmetic.hat.royal_crown.desc", "Mücevherlerle bezeli görkemli altın taç.");
        en("cosmetic.hat.royal_crown.desc", "Jeweled regal golden crown.");
        tr("cosmetic.hat.neko_headphones.title", "Neko Kulaklık");
        en("cosmetic.hat.neko_headphones.title", "Neko Headphones");
        tr("cosmetic.hat.neko_headphones.desc", "Işıklı kedi kulaklı oyuncu kulaklığı.");
        en("cosmetic.hat.neko_headphones.desc", "Glowing cat-ear gaming headset.");
        tr("cosmetic.hat.straw_pirate.title", "Hasır Korsan Şapkası");
        en("cosmetic.hat.straw_pirate.title", "Straw Pirate Hat");
        tr("cosmetic.hat.straw_pirate.desc", "Kırmızı şeritli ikonik hasır korsan şapkası.");
        en("cosmetic.hat.straw_pirate.desc", "Iconic straw hat with a crimson band.");
        tr("cosmetic.hat.ushanka_fur.title", "Kürklü Uşanka");
        en("cosmetic.hat.ushanka_fur.title", "Fur Ushanka");
        tr("cosmetic.hat.ushanka_fur.desc", "Soğuk kış geceleri için kalın kürklü kalpak.");
        en("cosmetic.hat.ushanka_fur.desc", "Thick fur-lined winter earflap cap.");
        tr("cosmetic.hat.tricorn_captain.title", "Korsan Şapkası");
        en("cosmetic.hat.tricorn_captain.title", "Pirate Tricorn");
        tr("cosmetic.hat.tricorn_captain.desc", "Altın şeritli ve kuru kafalı korsan şapkası.");
        en("cosmetic.hat.tricorn_captain.desc", "Gold-trimmed pirate tricorn with skull emblem.");
        tr("cosmetic.hat.napoleon_bicorne.title", "Napolyon Şapkası");
        en("cosmetic.hat.napoleon_bicorne.title", "Napoleon Bicorne");
        tr("cosmetic.hat.napoleon_bicorne.desc", "Fransız kokartlı ve altın şeritli iki köşeli şapka.");
        en("cosmetic.hat.napoleon_bicorne.desc", "Gold-trimmed bicorne hat with tricolor cockade.");
        tr("cosmetic.hat.egirl_headphones.title", "E-Girl Kulaklık");
        en("cosmetic.hat.egirl_headphones.title", "E-Girl Headphones");
        tr("cosmetic.hat.egirl_headphones.desc", "Pastel LED detaylı şık yayıncı kulaklığı.");
        en("cosmetic.hat.egirl_headphones.desc", "Sleek streamer headset with pastel LED accents.");

        // Neck
        tr("cosmetic.neck.crimson_scarf.title", "Kızıl Atkı");
        en("cosmetic.neck.crimson_scarf.title", "Crimson Scarf");
        tr("cosmetic.neck.crimson_scarf.desc", "Rüzgarda dalgalanan uzun kızıl savaşçı atkısı.");
        en("cosmetic.neck.crimson_scarf.desc", "Long crimson warrior scarf flowing in the wind.");
        tr("cosmetic.neck.arcane_pendant.title", "Büyülü Yıldız Kolye");
        en("cosmetic.neck.arcane_pendant.title", "Arcane Star Pendant");
        tr("cosmetic.neck.arcane_pendant.desc", "Göğüste parlayan rünlü yıldız tılsım.");
        en("cosmetic.neck.arcane_pendant.desc", "Glowing runic star talisman on the chest.");
        tr("cosmetic.neck.gold_chain.title", "Altın Zincir");
        en("cosmetic.neck.gold_chain.title", "Gold Chain");
        tr("cosmetic.neck.gold_chain.desc", "Kalın ve parlak klasik altın zincir.");
        en("cosmetic.neck.gold_chain.desc", "Heavy polished classic gold chain.");
        tr("cosmetic.neck.shadow_ninja_mask.title", "Gölge Ninja Maskesi");
        en("cosmetic.neck.shadow_ninja_mask.title", "Shadow Ninja Mask");
        tr("cosmetic.neck.shadow_ninja_mask.desc", "Alt yüzü gizleyen koyu kumaş suikastçı maskesi.");
        en("cosmetic.neck.shadow_ninja_mask.desc", "Dark cloth assassin half-mask.");
        tr("cosmetic.neck.oni_menpo.title", "Oni Menpo");
        en("cosmetic.neck.oni_menpo.title", "Oni Menpo");
        tr("cosmetic.neck.oni_menpo.desc", "Korkutucu dişli samuray zırh yüz maskesi.");
        en("cosmetic.neck.oni_menpo.desc", "Fierce fanged samurai armor face guard.");
        tr("cosmetic.neck.cool_sunglasses.title", "Güneş Gözlüğü");
        en("cosmetic.neck.cool_sunglasses.title", "Cool Sunglasses");
        tr("cosmetic.neck.cool_sunglasses.desc", "Yarı saydam camlı keskin hatlı güneş gözlüğü.");
        en("cosmetic.neck.cool_sunglasses.desc", "Sharp-framed translucent shades.");
        tr("cosmetic.neck.snow_scarf.title", "Kar Atkısı");
        en("cosmetic.neck.snow_scarf.title", "Snow Scarf");
        tr("cosmetic.neck.snow_scarf.desc", "Yumuşak örgü beyaz kış atkısı.");
        en("cosmetic.neck.snow_scarf.desc", "Cozy knitted white winter scarf.");
        tr("cosmetic.neck.skull_bandana.title", "Kafatası Bandanası");
        en("cosmetic.neck.skull_bandana.title", "Skull Bandana");
        tr("cosmetic.neck.skull_bandana.desc", "Çene desenli asi yüz bandanası.");
        en("cosmetic.neck.skull_bandana.desc", "Rebel face bandana with skull jaw print.");
        tr("cosmetic.neck.royal_brooch.title", "Kraliyet Broşu");
        en("cosmetic.neck.royal_brooch.title", "Royal Brooch");
        tr("cosmetic.neck.royal_brooch.desc", "Safir taşlı soylu yaka broşu.");
        en("cosmetic.neck.royal_brooch.desc", "Sapphire-set noble collar brooch.");
        tr("cosmetic.neck.ruby_choker.title", "Yakut Choker");
        en("cosmetic.neck.ruby_choker.title", "Ruby Choker");
        tr("cosmetic.neck.ruby_choker.desc", "Ortasında yakut taşlı kadife boyunluk.");
        en("cosmetic.neck.ruby_choker.desc", "Velvet choker with a centerpiece ruby.");
        tr("cosmetic.neck.silver_fang.title", "Gümüş Diş Kolye");
        en("cosmetic.neck.silver_fang.title", "Silver Fang Pendant");
        tr("cosmetic.neck.silver_fang.desc", "Kurt dişi biçiminde işlenmiş gümüş tılsım.");
        en("cosmetic.neck.silver_fang.desc", "Carved silver wolf-fang talisman.");

        // Hand
        tr("cosmetic.hand.fighter_wraps.title", "Dövüşçü Sargıları");
        en("cosmetic.hand.fighter_wraps.title", "Fighter Wraps");
        tr("cosmetic.hand.fighter_wraps.desc", "Bilek ve yumruklara sarılı dövüş bandajları.");
        en("cosmetic.hand.fighter_wraps.desc", "Combat bandages wrapped around wrists and knuckles.");
        tr("cosmetic.hand.sylas_chains.title", "Sylas Zincirleri");
        en("cosmetic.hand.sylas_chains.title", "Sylas Chains");
        tr("cosmetic.hand.sylas_chains.desc", "Bileklere dolanan ağır petrisit kelepçe zincirleri.");
        en("cosmetic.hand.sylas_chains.desc", "Heavy petricite shackle chains coiled on both arms.");
        tr("cosmetic.hand.combat_gloves.title", "Savaş Eldivenleri");
        en("cosmetic.hand.combat_gloves.title", "Combat Gloves");
        tr("cosmetic.hand.combat_gloves.desc", "Parmaksız zırhlı taktik dövüş eldivenleri.");
        en("cosmetic.hand.combat_gloves.desc", "Armored fingerless tactical combat gloves.");

        // Waist
        tr("cosmetic.waist.katana_sash.title", "Katana Kuşağı");
        en("cosmetic.waist.katana_sash.title", "Katana Sash");
        tr("cosmetic.waist.katana_sash.desc", "Belde kınında taşınan samuray katanası ve kuşağı.");
        en("cosmetic.waist.katana_sash.desc", "Sheathed samurai katana worn on a waist sash.");
        tr("cosmetic.waist.kitsune_tail.title", "Kitsune Kuyruğu");
        en("cosmetic.waist.kitsune_tail.title", "Kitsune Tail");
        tr("cosmetic.waist.kitsune_tail.desc", "Yürürken salınan kabarık tilki kuyruğu.");
        en("cosmetic.waist.kitsune_tail.desc", "Fluffy fox tail swaying with your movement.");
        tr("cosmetic.waist.demon_tail.title", "İblis Kuyruğu");
        en("cosmetic.waist.demon_tail.title", "Demon Tail");
        tr("cosmetic.waist.demon_tail.desc", "Sivri uçlu kıvrak iblis kuyruğu.");
        en("cosmetic.waist.demon_tail.desc", "Whip-like spade-tipped demon tail.");
        tr("cosmetic.waist.twin_daggers.title", "Çift Hançer");
        en("cosmetic.waist.twin_daggers.title", "Twin Daggers");
        tr("cosmetic.waist.twin_daggers.desc", "Belin iki yanında taşınan suikastçı hançerleri.");
        en("cosmetic.waist.twin_daggers.desc", "Dual assassin daggers holstered at the hips.");
        tr("cosmetic.waist.cat_tail.title", "Kedi Kuyruğu");
        en("cosmetic.waist.cat_tail.title", "Cat Tail");
        tr("cosmetic.waist.cat_tail.desc", "Kıvrımlı, sevimli sallanan tek kedi kuyruğu.");
        en("cosmetic.waist.cat_tail.desc", "Curved, lively swaying single cat tail.");
        tr("cosmetic.waist.wolf_tail.title", "Kurt Kuyruğu");
        en("cosmetic.waist.wolf_tail.title", "Wolf Tail");
        tr("cosmetic.waist.wolf_tail.desc", "Gür ve vahşi gri kurt kuyruğu.");
        en("cosmetic.waist.wolf_tail.desc", "Thick, bushy wild wolf tail.");
        tr("cosmetic.waist.disable_cape.on", "Pelerini Gizle: AÇIK");
        en("cosmetic.waist.disable_cape.on", "Disable Cape: ON");
        tr("cosmetic.waist.disable_cape.off", "Pelerini Gizle: KAPALI");
        en("cosmetic.waist.disable_cape.off", "Disable Cape: OFF");

        // Foot
        tr("cosmetic.foot.hermes_boots.title", "Hermes Çizmeleri");
        en("cosmetic.foot.hermes_boots.title", "Hermes Boots");
        tr("cosmetic.foot.hermes_boots.desc", "Yanlarında kanatçıklar bulunan efsanevi çizmeler.");
        en("cosmetic.foot.hermes_boots.desc", "Mythic winged boots with ankle feathers.");
        tr("cosmetic.foot.shinobi_wraps.title", "Shinobi Bacak Sargıları");
        en("cosmetic.foot.shinobi_wraps.title", "Shinobi Leg Wraps");
        tr("cosmetic.foot.shinobi_wraps.desc", "Sessiz adımlar için klasik ninja bacak bandajları.");
        en("cosmetic.foot.shinobi_wraps.desc", "Classic ninja leg bandages for swift movement.");
        tr("cosmetic.foot.fighter_leg_wraps.title", "Dövüşçü Bacak Sargıları");
        en("cosmetic.foot.fighter_leg_wraps.title", "Fighter Leg Wraps");
        tr("cosmetic.foot.fighter_leg_wraps.desc", "Tekme ve çeviklik için sıkı dövüş bandajları.");
        en("cosmetic.foot.fighter_leg_wraps.desc", "Tightly wound martial arts shin wraps.");
        tr("cosmetic.foot.bare_feet.title", "Ayak");
        en("cosmetic.foot.bare_feet.title", "Feet");
        tr("cosmetic.foot.bare_feet.desc", "Doğal cilt tonlu, kademeli 5 parmaklı ve eğimli ayak modeli.");
        en("cosmetic.foot.bare_feet.desc", "Natural skin-toned feet with arched instep and sculpted toes.");
        tr("cosmetic.foot.combat_boots.title", "Klasik Bot");
        en("cosmetic.foot.combat_boots.title", "Platform Boots");
        tr("cosmetic.foot.combat_boots.desc", "Mat siyah deri, dişli kalın taban ve gümüş tokalı botlar.");
        en("cosmetic.foot.combat_boots.desc", "Matte charcoal leather boots with lugged soles and silver buckles.");
        tr("cosmetic.foot.classic_sneakers.title", "Klasik Sneaker");
        en("cosmetic.foot.classic_sneakers.title", "Classic Sneakers");
        tr("cosmetic.foot.classic_sneakers.desc", "Kırık beyaz ve arduvaz çift tonlu, beyaz tabanlı günlük sneaker.");
        en("cosmetic.foot.classic_sneakers.desc", "Two-tone off-white and slate low-top sneakers with white cupsoles.");

        // Trail Effects
        tr("cosmetic.trail.jump_magic_circle.title", "Zıplama Büyü Çemberi");
        en("cosmetic.trail.jump_magic_circle.title", "Jump Magic Circle");
        tr("cosmetic.trail.jump_magic_circle.desc", "Zıpladığında ayaklarının altında rün çemberi belirir.");
        en("cosmetic.trail.jump_magic_circle.desc", "Spawns a glowing runic circle beneath you on jump.");
        tr("cosmetic.trail.shadow_smoke.title", "Gölge Dumanı");
        en("cosmetic.trail.shadow_smoke.title", "Shadow Smoke");
        tr("cosmetic.trail.shadow_smoke.desc", "Adımlarının ardından koyu gölge sisleri bırakır.");
        en("cosmetic.trail.shadow_smoke.desc", "Leaves dark shadow wisps trailing behind your steps.");
        tr("cosmetic.trail.snowflake_aura.title", "Kar Tanesi Aurası");
        en("cosmetic.trail.snowflake_aura.title", "Snowflake Aura");
        tr("cosmetic.trail.snowflake_aura.desc", "Etrafında süzülen soğuk kar kristalleri.");
        en("cosmetic.trail.snowflake_aura.desc", "Delicate ice crystals drifting around your feet.");
        tr("cosmetic.trail.crimson_petals.title", "Kızıl Yapraklar");
        en("cosmetic.trail.crimson_petals.title", "Crimson Petals");
        tr("cosmetic.trail.crimson_petals.desc", "Rüzgarda savrulan kızıl sakura yaprakları.");
        en("cosmetic.trail.crimson_petals.desc", "Crimson blossom petals swirling in the wind.");

        // --- UPDATE REMINDER ---
        tr("update.banner.title", "Yeni Güncelleme Mevcut");
        en("update.banner.title", "New Update Available");
        tr("update.banner.desc", "Kiem Addons için yeni sürüm yayınlandı:");
        en("update.banner.desc", "A new version of Kiem Addons is out:");
        tr("update.banner.download", "İndir ↗");
        en("update.banner.download", "Download ↗");
        tr("features.update_checker.title", "Güncelleme Denetleyicisi");
        en("features.update_checker.title", "Update Checker");
        tr("features.update_checker.desc", "F6 menüsünde Modrinth üzerinden yeni sürüm bildirimi gösterir.");
        en("features.update_checker.desc", "Shows a non-intrusive update reminder in the F6 menu via Modrinth.");
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
