.PHONY: clean clean-build all

all: ParseRegHex2.class GrepReg.class

ParseRegHex2.class: reg-hex2_parsing/ParseRegHex2.java
	javac -d . $^

GrepReg.class: reg-hex2_parsing/GrepReg.java
	javac -d . $^

clean-build: clean all

clean:
	-find . -type f -name "*.class" -print -delete
	-rm -f *.outreg

