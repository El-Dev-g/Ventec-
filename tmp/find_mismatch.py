import sys

def find_mismatch(filepath):
    with open(filepath, 'r') as f:
        lines = f.readlines()
    
    depth = 0
    for i, line in enumerate(lines):
        line_num = i + 1
        for char in line:
            if char == '{':
                depth += 1
            elif char == '}':
                depth -= 1
        
        if line_num >= 1830 and line_num <= 3000:
             print(f"Line {line_num} depth: {depth}")
    
    print(f"Final depth: {depth}")


if __name__ == "__main__":
    find_mismatch(sys.argv[1])
