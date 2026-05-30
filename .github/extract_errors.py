import re
INPUT="build_log.txt"; OUTPUT="error.txt"
PATTERNS=[re.compile(p,re.I) for p in [r"FAILURE:",r"What went wrong",r"Caused by",r"error:",r"^e: ",r"ndk",r"cmake",r"Could not",r"Execution failed",r"> Task .* FAILED",r"Exception",r"No such",r"not found",r"BUILD FAILED",r"Unresolved",r"cannot find symbol"]]
def main():
    try: lines=open(INPUT,encoding="utf-8",errors="replace").readlines()
    except FileNotFoundError:
        open(OUTPUT,"w").write("build_log.txt not found\n"); return
    out=[]
    for i,l in enumerate(lines):
        s=l.rstrip("\n")
        for p in PATTERNS:
            if p.search(s):
                out.append("\n".join(x.rstrip("\n") for x in lines[max(0,i-1):i+5])); out.append("-"*60); break
    with open(OUTPUT,"w",encoding="utf-8") as f:
        f.write("NX Launcher (FCL base) build errors\n"+"="*60+"\n\n"+"\n".join(out)+"\n" if out else "No matching error lines found.\n")
if __name__=="__main__": main()
