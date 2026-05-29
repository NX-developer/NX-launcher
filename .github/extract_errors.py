import re

INPUT = "build_log.txt"
OUTPUT = "error.txt"

PATTERNS = [
    re.compile(r"FAILURE:"),
    re.compile(r"What went wrong:"),
    re.compile(r"Caused by:"),
    re.compile(r"error:", re.IGNORECASE),
    re.compile(r"^e: "),
    re.compile(r"ndk", re.IGNORECASE),
    re.compile(r"Android\.mk"),
    re.compile(r"Could not"),
    re.compile(r"Execution failed"),
    re.compile(r"> Task .* FAILED"),
    re.compile(r"Exception"),
    re.compile(r"No such"),
    re.compile(r"not found", re.IGNORECASE),
    re.compile(r"BUILD FAILED"),
]


def main():
    try:
        with open(INPUT, "r", encoding="utf-8", errors="replace") as f:
            lines = f.readlines()
    except FileNotFoundError:
        with open(OUTPUT, "w", encoding="utf-8") as out:
            out.write("build_log.txt not found\n")
        return

    collected = []
    for index, line in enumerate(lines):
        stripped = line.rstrip("\n")
        for pattern in PATTERNS:
            if pattern.search(stripped):
                start = max(0, index - 1)
                end = min(len(lines), index + 5)
                block = [l.rstrip("\n") for l in lines[start:end]]
                collected.append("\n".join(block))
                collected.append("-" * 60)
                break

    with open(OUTPUT, "w", encoding="utf-8") as out:
        if collected:
            out.write("NX Launcher (engine) build errors\n")
            out.write("=" * 60 + "\n\n")
            out.write("\n".join(collected))
            out.write("\n")
        else:
            out.write("No matching error lines were found in the build log.\n")


if __name__ == "__main__":
    main()
