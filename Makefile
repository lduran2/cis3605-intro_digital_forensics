.PHONY: clean clean-build

ParseRegHex2.class: reg-hex2_parsing/ParseRegHex2.java
	javac -d . $^

clean-build: clean ParseRegHex2.class

clean:
	-find . -type f -name *.class -print -delete
	-rm -f *.outreg

