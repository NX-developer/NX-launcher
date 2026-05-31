#!/usr/bin/env python3
import os
import re
import sys

NX_GREEN = "#5CDB6B"
NX_GREEN_DARK = "#3BA84A"
NX_BG = (11, 14, 12)
APP_NAME = "NX Launcher"
APP_ID = "com.nxlauncher"
REPO = "NX-developer/NX-launcher"
VERSION_MAP_URL = "https://raw.githubusercontent.com/NX-developer/NX-launcher/main/version_map.json"
RELEASES_LATEST = "https://github.com/NX-developer/NX-launcher/releases/latest"
NX_REPO_URL = "https://github.com/NX-developer/NX-launcher"

ROOT = os.path.abspath(sys.argv[1]) if len(sys.argv) > 1 else os.getcwd()

ok = 0
warn = 0
warnings = []


def p(path):
    return os.path.join(ROOT, path)


def status(applied, desc):
    global ok, warn
    if applied:
        ok += 1
        print("  OK   " + desc)
    else:
        warn += 1
        warnings.append(desc)
        print("  WARN not found: " + desc)


def replace_in(rel, old, new, desc, count=0):
    fp = p(rel)
    if not os.path.exists(fp):
        status(False, desc + " (missing file " + rel + ")")
        return
    s = open(fp, encoding="utf-8").read()
    if old in s:
        s = s.replace(old, new) if count == 0 else s.replace(old, new, count)
        open(fp, "w", encoding="utf-8").write(s)
        status(True, desc)
    else:
        status(new in s, desc + " (already applied)" if new in s else desc)


def regex_in(rel, pattern, repl, desc, flags=0):
    fp = p(rel)
    if not os.path.exists(fp):
        status(False, desc + " (missing file " + rel + ")")
        return
    s = open(fp, encoding="utf-8").read()
    ns, n = re.subn(pattern, repl, s, flags=flags)
    if n > 0:
        open(fp, "w", encoding="utf-8").write(ns)
        status(True, desc)
    else:
        status(False, desc)


def overwrite(rel, content, desc):
    fp = p(rel)
    os.makedirs(os.path.dirname(fp), exist_ok=True)
    open(fp, "w", encoding="utf-8").write(content)
    status(True, desc)


def replace_color_tree(dirs, targets, new, desc):
    hit = False
    for d in dirs:
        base = p(d)
        if not os.path.isdir(base):
            continue
        for r, _, files in os.walk(base):
            for f in files:
                if not f.endswith((".java", ".kt", ".xml")):
                    continue
                fp = os.path.join(r, f)
                s = open(fp, encoding="utf-8").read()
                orig = s
                for t in targets:
                    s = s.replace(t, new)
                if s != orig:
                    open(fp, "w", encoding="utf-8").write(s)
                    hit = True
    status(hit, desc)


FCL_VALUES = "FCL/src/main/res/values/strings.xml"
LIB_VALUES = "FCLLibrary/src/main/res/values/strings.xml"
GRADLE = "FCL/build.gradle.kts"
THEME = "FCLLibrary/src/main/java/com/tungsten/fcllibrary/component/theme/Theme.java"
THEME_ENGINE = "FCLLibrary/src/main/java/com/tungsten/fcllibrary/component/theme/ThemeEngine.java"
ABOUT = "FCL/src/main/java/com/tungsten/fcl/ui/setting/AboutPage.java"
UPDATE_CHECKER = "FCL/src/main/java/com/tungsten/fcl/upgrade/UpdateChecker.java"
UPDATE_DIALOG = "FCL/src/main/java/com/tungsten/fcl/upgrade/UpdateDialog.java"
MAIN_UI = "FCL/src/main/java/com/tungsten/fcl/ui/main/MainUI.java"


def text_changes():
    print("[1] App identity")
    replace_in(FCL_VALUES,
               '<string name="app_name" translatable="false">Fold Craft Launcher</string>',
               '<string name="app_name" translatable="false">' + APP_NAME + '</string>',
               "app_name -> NX Launcher")
    replace_in(GRADLE, 'applicationId = "com.tungsten.fcl"',
               'applicationId = "' + APP_ID + '"', "applicationId -> com.nxlauncher")
    replace_in(GRADLE,
               '"FCL-${variant.buildType}-${defaultConfig.versionName}-${abi}.apk"',
               '"NX-Launcher-${variant.buildType}-${defaultConfig.versionName}-${abi}.apk"',
               "APK output filename -> NX-Launcher")

    print("[2] Theme")
    replace_color_tree(["FCL/src", "FCLLibrary/src"], ["7797CF", "7797cf"],
                       NX_GREEN.lstrip("#"), "theme accent #7797CF -> NX green")
    regex_in(THEME_ENGINE,
             r"public static boolean isNightMode\(Context context\) \{\s*return [^;]*?;\s*\}",
             "public static boolean isNightMode(Context context) {\n        return true;\n    }",
             "force dark (isNightMode -> true)", flags=re.S)
    replace_in("FCL/src/main/res/values/colors.xml",
               '<color name="icon_background_color">#FFFFFF</color>',
               '<color name="icon_background_color">' + NX_GREEN_DARK + '</color>',
               "icon_background_color (values) -> NX")
    replace_in("FCL/src/main/res/values-v31/colors.xml",
               '<color name="icon_background_color">@android:color/system_accent1_0</color>',
               '<color name="icon_background_color">' + NX_GREEN_DARK + '</color>',
               "icon_background_color (v31) -> NX")

    print("[3] Branding strings")
    replace_in(FCL_VALUES, "Welcome to Fold Craft Launcher",
               "Welcome to " + APP_NAME, "splash_title")
    replace_in(FCL_VALUES,
               '<string name="about_launcher" translatable="false">Fold Craft Launcher</string>',
               '<string name="about_launcher" translatable="false">' + APP_NAME + '</string>',
               "about_launcher")
    replace_in(FCL_VALUES,
               '<string name="about_developer" translatable="false">FCL-Team</string>',
               '<string name="about_developer" translatable="false">NX-developer</string>',
               "about_developer")
    regex_in(FCL_VALUES, r'(<string name="about_desc">).*?(</string>)',
             lambda m: m.group(1) + ABOUT_DESC + m.group(2), "about_desc", flags=re.S)
    replace_in(FCL_VALUES,
               "No storage permission, please grant Fold Craft Launcher the permission.",
               "No storage permission, please grant " + APP_NAME + " the permission.",
               "permission msg")
    replace_in(FCL_VALUES,
               "FCL requires storage permission to manage and read game files. FCL is fully open-source on GitHub",
               APP_NAME + " requires storage permission to manage and read game files. It is fully open-source on GitHub",
               "splash_agreement")
    replace_in(FCL_VALUES, "FCL requires permission to post notifications",
               APP_NAME + " requires permission to post notifications", "notification permission")
    replace_in(FCL_VALUES, "Browse FCL logs", "Browse NX logs", "folder log")
    replace_in(FCL_VALUES, "Upload FCL log", "Upload NX log", "upload log")
    replace_in(FCL_VALUES, "FCL can still import it", APP_NAME + " can still import it", "modpack import")
    replace_in(FCL_VALUES, "in-game FCL menu", "in-game NX menu", "terracotta menu")
    replace_in(FCL_VALUES, "FCL - Multiplayer", APP_NAME + " - Multiplayer", "terracotta title")
    replace_in(LIB_VALUES,
               "Fold Craft Launcher has encountered an unresolvable error",
               APP_NAME + " has encountered an unresolvable error", "crash hint")
    overwrite("FCL/src/main/assets/eula.txt", EULA, "eula.txt -> NX GPL-3.0")
    replace_in(ABOUT, '"https://github.com/FCL-Team/FoldCraftLauncher"',
               '"' + NX_REPO_URL + '"', "About source link -> NX repo")

    print("[4] Updater -> NX")
    replace_in(UPDATE_CHECKER,
               "https://raw.githubusercontent.com/FCL-Team/FoldCraftLauncher/main/version_map.json",
               VERSION_MAP_URL, "update url")
    replace_in(UPDATE_CHECKER,
               "https://gitee.com/fcl-team/FCL-Repo/raw/main/res/version_map.json",
               VERSION_MAP_URL, "update url (cn)")
    replace_in(UPDATE_DIALOG, '"FoldCraftLauncher.apk"', '"NX-Launcher.apk"', "apk cache name")
    replace_in(UPDATE_DIALOG, 'task.setName("FoldCraftLauncher")', 'task.setName("NX-Launcher")', "task name")
    replace_in(UPDATE_DIALOG,
               "https://github.com/FCL-Team/FoldCraftLauncher/releases/latest",
               RELEASES_LATEST, "update fallback link")
    regex_in(UPDATE_DIALOG,
             r'private String getTargetArchUrl\(\) \{.*?return url;\s*\}',
             "private String getTargetArchUrl() {\n        return version.getUrl();\n    }",
             "getTargetArchUrl -> universal url", flags=re.S)

    print("[5] Remove announcement")
    regex_in(MAIN_UI, r'private void checkAnnouncement\(\) \{.*?\n    \}',
             "private void checkAnnouncement() {\n        announcementContainer.setVisibility(View.GONE);\n    }",
             "checkAnnouncement -> no-op", flags=re.S)


def gen_assets():
    print("[6] Icons and background (PIL)")
    try:
        from PIL import Image, ImageDraw, ImageFont, ImageFilter
    except Exception as e:
        status(False, "PIL not available: " + str(e))
        return
    font_path = "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf"
    GT = (92, 219, 107)
    GB = (59, 168, 74)
    SS = 4
    res = "FCL/src/main/res"
    dens = {"mdpi": (108, 48), "hdpi": (162, 72), "xhdpi": (216, 96),
            "xxhdpi": (324, 144), "xxxhdpi": (432, 192)}

    def grad(size):
        img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        px = img.load()
        for y in range(size):
            t = y / max(1, size - 1)
            c = (int(GT[0] + (GB[0] - GT[0]) * t), int(GT[1] + (GB[1] - GT[1]) * t),
                 int(GT[2] + (GB[2] - GT[2]) * t), 255)
            for x in range(size):
                px[x, y] = c
        return img

    def fg(size):
        big = size * SS
        o = Image.new("RGBA", (big, big), (0, 0, 0, 0))
        d = ImageDraw.Draw(o)
        f = ImageFont.truetype(font_path, int(big * 0.34))
        d.text((big // 2, int(big * 0.50)), "NX", font=f, fill=(255, 255, 255, 255), anchor="mm")
        return o.resize((size, size), Image.LANCZOS)

    def round_icon(size):
        big = size * SS
        g = grad(big)
        m = Image.new("L", (big, big), 0)
        ImageDraw.Draw(m).ellipse([0, 0, big - 1, big - 1], fill=255)
        o = Image.new("RGBA", (big, big), (0, 0, 0, 0))
        o.paste(g, (0, 0), m)
        d = ImageDraw.Draw(o)
        f = ImageFont.truetype(font_path, int(big * 0.40))
        d.text((big // 2, int(big * 0.52)), "NX", font=f, fill=(255, 255, 255, 255), anchor="mm")
        return o.resize((size, size), Image.LANCZOS)

    for dn, (fgs, rs) in dens.items():
        d = p(res + "/mipmap-" + dn)
        if not os.path.isdir(d):
            os.makedirs(d, exist_ok=True)
        fg(fgs).save(os.path.join(d, "ic_launcher_foreground.webp"), "WEBP", quality=95)
        round_icon(rs).save(os.path.join(d, "ic_launcher_round.png"))
    status(True, "launcher icons (foreground webp + round png)")

    W, H = 1920, 1080
    img = Image.new("RGB", (W, H), NX_BG)
    px = img.load()
    for y in range(H):
        t = y / (H - 1)
        r = int(17 + (7 - 17) * t)
        gg = int(22 + (9 - 22) * t)
        b = int(15 + (7 - 15) * t)
        for x in range(W):
            px[x, y] = (r, gg, b)
    glow = Image.new("RGB", (W, H), (0, 0, 0))
    ImageDraw.Draw(glow).ellipse([-400, H - 700, 700, H + 400], fill=GT)
    glow = glow.filter(ImageFilter.GaussianBlur(260))
    base = img.load()
    gl = glow.load()
    for y in range(H):
        for x in range(W):
            gr, ggn, gb = gl[x, y]
            if gr or ggn or gb:
                br, bg2, bb = base[x, y]
                a = 0.10
                base[x, y] = (min(255, int(br + gr * a)), min(255, int(bg2 + ggn * a)),
                              min(255, int(bb + gb * a)))
    bgdir = p("FCLLibrary/src/main/res/drawable")
    if os.path.isdir(bgdir):
        img.save(os.path.join(bgdir, "background_dark.jpg"), "JPEG", quality=88)
        img.save(os.path.join(bgdir, "background_light.jpg"), "JPEG", quality=88)
        status(True, "dark NX background (dark + light)")
    else:
        status(False, "drawable dir for background")


ABOUT_DESC = ("NX Launcher is an Android Minecraft launcher that lets you play Minecraft: Java Edition on Android."
              "\\n\\nNX Launcher is built on Fold Craft Launcher by FCL-Team."
              "\\n\\nMost features are based on HMCLCore, and it borrows code from PojavLauncher and Boat."
              " If you distribute this app, please follow the GPL-v3 License.")

EULA = """NX Launcher end-user license agreement

Before using the software, please read this agreement first.
NX Launcher is built on Fold Craft Launcher by FCL-Team and is maintained by NX-developer.

1. User's obligations
1) Get a qualified device. NX Launcher requires Android 8.0 or higher.
2) Pay for the cost of Internet access and other services.
3) Take responsibility for all the consequences of your own use of the software.

2. Disclaimer
1) The risk caused by the user's use of this software shall be borne by the user.
2) The software is not guaranteed to meet all user needs.
3) The software is not responsible for the correctness, security, legality, etc. of third-party services.
4) The developer is not responsible for any problems caused by users' use of third-party services, and will not and cannot assume any legal responsibility.

3. Copyright and license
1) NX Launcher is free and open source software, released under the GNU General Public License v3.0 (GPL-3.0).
2) NX Launcher is based on Fold Craft Launcher by FCL-Team, with features based on HMCLCore and code from PojavLauncher and Boat. These components remain the property of their respective copyright owners.
3) If you redistribute this software, you must comply with the GPL-3.0 license.
"""


def main():
    print("NX-ify FCL  ->  root: " + ROOT)
    if not os.path.isdir(p("FCL")) or not os.path.isdir(p("FCLLibrary")):
        print("ERROR: this does not look like an FCL source tree (no FCL/ or FCLLibrary/).")
        sys.exit(1)
    text_changes()
    gen_assets()
    print("\nDone. applied=%d  warnings=%d" % (ok, warn))
    if warnings:
        print("Needs manual check (FCL may have changed):")
        for w in warnings:
            print("  - " + w)


if __name__ == "__main__":
    main()
