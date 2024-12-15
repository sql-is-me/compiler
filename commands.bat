cat llvm_ir.txt > test.ll
llvm-link test.ll lib.ll -S -o out.ll
llvm-as out.ll -o test.bc
clang test.bc -o test.exe

(
    echo 1
    echo -1
    echo 0
) | .\test.exe

(
    echo 100
    echo -101
    echo x
) | .\test.exe