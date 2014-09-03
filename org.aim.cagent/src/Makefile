# if there is JAVA_HOME var file then include it
ifneq (, $(wildcard javahome/var))
include javahome/var
endif

BUILDDIR=./target/

# Source lists
LIBNAME=CEventAgent
SOURCES=CEventAgent.c

# Library name and options needed to build it
UNAME := $(shell uname)
ifeq ($(UNAME), Linux)
LIBRARY=lib$(LIBNAME).so
JNI_OS=linux
else ifeq ($(UNAME), Darwin)
LIBRARY=lib$(LIBNAME).jnilib
JNI_OS=darwin
else ifeq ($(OS), Windows_NT)
LIBRARY=lib$(LIBNAME).dll
JNI_OS=win32
CC=cl
else
$(error error: unsupported operating system: $(UNAME))
endif

# Object files needed to create library
ifeq ($(OS), Windows_NT)
OBJECTS=$(LIBNAME).obj
else
OBJECTS=$(SOURCES:%.c=%.o)
endif

ifeq ($(OS), Windows_NT)
VS_DIR = "C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC"
VS_LIB = $(VS_DIR)\lib\amd64
VS_INCLUDE = $(VS_DIR)\include
WIN_SDK = "C:\Program Files\Microsoft SDKs\Windows\v7.0\"

CFLAGS = /MD /Zi /Ox /Os /Gy /c
CFLAGS += /I$(VS_LIB) /I$(VS_INCLUDE) /I"$(JAVA_HOME)\include" /I"$(JAVA_HOME)\include\win32"

LFLAGS = -dll /LIBPATH:$(VS_LIB) /LIBPATH:$(WIN_SDK)\Lib\x64
else
# Building a shared library
LINK_SHARED=$(LINK.c) -shared -o $@

# GNU Compiler options needed to build it
COMMON_FLAGS=-fPIC
# Options that help find errors
COMMON_FLAGS+= -W -Wall -Wextra -O3 -Wno-unused-parameter

CFLAGS += $(COMMON_FLAGS)

CFLAGS += -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/$(JNI_OS)
endif

# add debugging output
ifeq ($(DEBUG), TRUE)
CFLAGS += -DDEBUG
endif

build: $(LIBRARY)
	cp $(LIBRARY) $(BUILDDIR)

ifeq ($(OS), Windows_NT)
$(LIBRARY): $(OBJECTS)
	link $(LFLAGS) -out:$(LIBRARY) $(OBJECTS)

$(OBJECTS): $(SOURCES)
	cl $(CFLAGS) $(SOURCES)
else
# Build native library
$(LIBRARY): $(OBJECTS)
	$(LINK_SHARED) $(OBJECTS) $(LIBRARIES)
endif

# Cleanup the built bits
clean:
	rm -f $(LIBRARY) $(OBJECTS)

debug:
	$(MAKE) DEBUG=TRUE

all: build
