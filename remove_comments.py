import os
import re

base_dir = "/Users/matteosomensi/MatteoProjects/pokedex"

# regex to find // comments, but not if they are part of a URL (e.g. http://) or inside a string.
# a simple approach is to look for // and assume it's a comment if it has a space before it or is at the start of the line.
def remove_inline_comments(line):
    # Very simple heuristic: if line contains // and not http:// or https://
    # Actually, we can just split by // if it's not preceded by :
    # A robust way is to use a simple state machine or regex for Kotlin comments
    # To be safe, we'll strip lines that start with // (after whitespace)
    stripped = line.lstrip()
    if stripped.startswith("//"):
        return ""
    
    # if it has // at the end, let's just remove it if it's not http
    if "://" not in line and "//" in line:
        idx = line.find("//")
        # only if there's a space before // to avoid removing things inside strings like "a//b"
        if idx > 0 and line[idx-1] in [" ", "\t"]:
            return line[:idx].rstrip() + "\n"
    
    return line

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                lines = f.readlines()
            
            new_lines = []
            for line in lines:
                new_line = remove_inline_comments(line)
                if new_line != "":
                    new_lines.append(new_line)
                    
            with open(path, "w") as f:
                f.writelines(new_lines)
