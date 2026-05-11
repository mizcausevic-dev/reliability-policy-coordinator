from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "screenshots"
OUT.mkdir(exist_ok=True)

BG = "#09121f"
SHELL = "#131d30"
CARD = "#1a2740"
BORDER = "#294164"
TEXT = "#f3eee1"
MUTED = "#b7c2d7"
ACCENT = "#86c4ff"
GOLD = "#f4e4a2"
PINK = "#ffb5df"
GREEN = "#89e9ae"


def font(name: str, size: int):
    return ImageFont.truetype(str(Path("C:/Windows/Fonts") / name), size)


DISPLAY = font("georgiab.ttf", 40)
TITLE = font("segoeuib.ttf", 23)
BODY = font("segoeui.ttf", 18)
SMALL = font("segoeui.ttf", 14)
LABEL = font("consola.ttf", 14)


def wrap(draw, text, face, width):
    words = text.split()
    lines = []
    line = ""
    for word in words:
        trial = word if not line else f"{line} {word}"
        if draw.textlength(trial, font=face) <= width:
            line = trial
        else:
            if line:
                lines.append(line)
            line = word
    if line:
        lines.append(line)
    return lines


def shell():
    img = Image.new("RGB", (1400, 900), BG)
    draw = ImageDraw.Draw(img)
    draw.rounded_rectangle((28, 28, 1372, 872), 28, fill=SHELL, outline=BORDER, width=2)
    return img, draw


def block(draw, x, y, text, face, fill, width, gap=8):
    cursor = y
    for line in wrap(draw, text, face, width):
        draw.text((x, cursor), line, font=face, fill=fill)
        cursor += face.size + gap
    return cursor


def metric(draw, x, y, w, h, label, value, subtitle):
    draw.rounded_rectangle((x, y, x + w, y + h), 22, fill=CARD, outline=BORDER, width=2)
    draw.text((x + 18, y + 18), label.upper(), font=LABEL, fill=ACCENT)
    draw.text((x + 18, y + 56), value, font=DISPLAY, fill=GOLD)
    block(draw, x + 18, y + 118, subtitle, BODY, MUTED, w - 36, 6)


def card(draw, x, y, w, h, label, title, body, footer):
    draw.rounded_rectangle((x, y, x + w, y + h), 22, fill=CARD, outline=BORDER, width=2)
    draw.text((x + 18, y + 18), label.upper(), font=LABEL, fill=ACCENT)
    block(draw, x + 18, y + 54, title, TITLE, TEXT, w - 36, 6)
    block(draw, x + 18, y + 122, body, BODY, MUTED, w - 36, 6)
    block(draw, x + 18, y + h - 86, footer, TITLE, GOLD, w - 36, 6)


def hero():
    img, draw = shell()
    draw.text((62, 74), "RELIABILITY POLICY COORDINATOR", font=LABEL, fill=ACCENT)
    block(draw, 62, 122, "Turn service pressure into a policy order leaders can actually act on.", DISPLAY, TEXT, 1220, 10)
    block(draw, 62, 246, "Kotlin is carrying dependency drag review, error-budget pressure, freeze-window timing, and rollback guidance in one JVM service layer.", BODY, MUTED, 1180, 8)
    metric(draw, 62, 376, 290, 180, "open incidents", "3", "Three modeled services are currently forcing policy decisions.")
    metric(draw, 376, 376, 290, 180, "policy breaches", "2", "Two incident paths already need explicit escalation or freeze control.")
    metric(draw, 690, 376, 290, 180, "freeze conflicts", "2", "Two threads are approaching release timing windows too quickly.")
    metric(draw, 1004, 376, 290, 180, "rollback ready", "2", "Most lanes can recover, but not all of them can do it safely.")
    draw.rounded_rectangle((62, 610, 1294, 790), 24, fill=CARD, outline=BORDER, width=2)
    draw.text((92, 640), "CURRENT POLICY", font=LABEL, fill=PINK)
    block(draw, 92, 680, "Freeze the Northstar release lane before dependency drag turns a reliability problem into a commercial outage.", TITLE, TEXT, 1130, 6)
    block(draw, 92, 744, "This repo treats reliability as a policy surface, not just a chart-reading exercise.", BODY, MUTED, 1100, 6)
    img.save(OUT / "01-hero.png")


def lanes():
    img, draw = shell()
    draw.text((62, 74), "POLICY LANES", font=LABEL, fill=ACCENT)
    block(draw, 62, 122, "Three service lanes. One coordinator deciding how hard the response needs to be.", DISPLAY, TEXT, 1220, 10)
    card(draw, 62, 266, 390, 252, "CHECKOUT RUNTIME", "Freeze the release lane before burn outruns rollback posture.", "Budget burn, dependency drag, and timing pressure are stacking in one lane.", "Shift to rollback.")
    card(draw, 505, 266, 390, 252, "IDENTITY SYNC", "Protect the regional freeze window without overreacting.", "The worker is recoverable, but the remaining legacy tenant contract cannot be ignored.", "Keep rollback ready and reroute.")
    card(draw, 948, 266, 346, 252, "OBS EXPORTER", "Hold in watch mode.", "The exporter is noisy, but policy pressure is still contained.", "Recheck next cycle.")
    draw.rounded_rectangle((62, 566, 1294, 806), 22, fill=CARD, outline=BORDER, width=2)
    draw.text((92, 594), "WHY KOTLIN FITS", font=LABEL, fill=PINK)
    block(draw, 92, 634, "This repo uses Kotlin where it shines: a compact JVM backend, clean policy logic, and service-shaped reliability decisions.", TITLE, TEXT, 1120, 6)
    img.save(OUT / "02-policy-lanes.png")


def escalation():
    img, draw = shell()
    draw.text((62, 74), "ESCALATION VIEW", font=LABEL, fill=ACCENT)
    block(draw, 62, 122, "One incident, broken into service pressure, owner lane, and freeze guidance.", DISPLAY, TEXT, 1220, 10)
    draw.rounded_rectangle((62, 250, 510, 790), 22, fill=CARD, outline=BORDER, width=2)
    draw.text((92, 278), "REL-7102", font=LABEL, fill=PINK)
    block(draw, 92, 318, "Northstar checkout path is burning budget faster than the rollback lane can absorb.", TITLE, TEXT, 380, 6)
    draw.text((92, 420), "TIMELINE", font=LABEL, fill=ACCENT)
    for index, item in enumerate([
        "18:05 ET - Tail latency crossed p99 threshold.",
        "18:18 ET - Retry pressure started eating budget.",
        "18:31 ET - GTM leadership asked for a decision."
    ]):
        block(draw, 92, 454 + index * 78, item, BODY, MUTED, 380, 6)
    draw.rounded_rectangle((566, 250, 760, 790), 22, fill=CARD, outline=BORDER, width=2)
    draw.text((596, 278), "STATUS", font=LABEL, fill=ACCENT)
    draw.text((596, 318), "ESCALATE", font=DISPLAY, fill=GOLD)
    draw.text((596, 406), "RISK SCORE 100", font=TITLE, fill=TEXT)
    draw.text((596, 456), "OWNER revenue-systems-lead", font=BODY, fill=MUTED)
    block(draw, 596, 494, "FREEZE releases until rollback posture improves.", BODY, MUTED, 620, 6)
    draw.text((596, 560), "NEXT ACTION", font=LABEL, fill=PINK)
    block(draw, 596, 600, "Freeze the release lane and shift traffic to the resilient fallback path.", TITLE, TEXT, 600, 6)
    img.save(OUT / "03-escalation.png")


def proof():
    img, draw = shell()
    draw.text((62, 74), "VALIDATION PROOF", font=LABEL, fill=ACCENT)
    block(draw, 62, 122, "Real routes. Real JSON. Real Kotlin/JVM validation.", DISPLAY, TEXT, 1220, 10)
    draw.rounded_rectangle((62, 262, 738, 780), 22, fill="#0d1728", outline=BORDER, width=2)
    draw.text((92, 292), "> POST /api/analyze/policy", font=TITLE, fill=GREEN)
    code = [
        "{",
        '  "severity": "critical",',
        '  "dependencyDrag": 4,',
        '  "errorBudgetBurn": 0.94,',
        '  "freezeWindowHours": 1.5',
        "}"
    ]
    y = 338
    for line in code:
        draw.text((92, y), line, font=BODY, fill=TEXT)
        y += 34
    draw.rounded_rectangle((790, 262, 1294, 780), 22, fill=CARD, outline=BORDER, width=2)
    draw.text((818, 292), "PROOF POINTS", font=LABEL, fill=ACCENT)
    block(draw, 818, 336, "Gradle tests are green, the root and docs routes boot cleanly, and the sample analysis returns a real freeze recommendation.", TITLE, TEXT, 430, 8)
    draw.text((818, 506), "JVM runtime", font=BODY, fill=GREEN)
    draw.text((818, 540), "Kotlin 2.0 on Java 21", font=BODY, fill=MUTED)
    draw.text((818, 600), "API routes", font=BODY, fill=GREEN)
    block(draw, 818, 634, "/, /docs, /api/dashboard/summary, /api/incidents, /api/sample, /api/analyze/policy", BODY, MUTED, 420, 6)
    img.save(OUT / "04-proof.png")


if __name__ == "__main__":
    hero()
    lanes()
    escalation()
    proof()
