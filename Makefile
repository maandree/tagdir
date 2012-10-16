all:
	mkdir -p bin
	javac -cp src -s src -d bin $$(find ./ | grep \\.java\$$)

clean:
	rm -r bin

.PHONY: clean
