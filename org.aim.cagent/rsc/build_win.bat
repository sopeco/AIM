:: %Visual Studio%\VC\lib,
:: %Visual Studio%\VC\redist\<System>\Microsoft.VC<Version>.CRT and
:: %Microsoft SDKs$\Windows\<Version>\Lib\<System>
:: have to be specified in the PATH variable.
:: (e.g.,
:: C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\lib\amd64
:: C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\redist\amd64\Microsoft.VC90.CRT
:: C:\Program Files\Microsoft SDKs\Windows\v7.0\Lib\x64 )
:: Ensure that the Visual Studio and Java are either both x86 or both x64!

:: Has to be set depending on the installation of Visual Studio and the system (x86 / x64)
set VS_DIR="C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC"
set WIN_LIB="C:\Program Files\Microsoft SDKs\Windows\v7.0\Lib\x64"

set TARGET=..\target
set SRC=../src
set LIBRARY=libCEventAgent.dll
set OBJECT=CEventAgent.obj
set SOURCE=%SRC%/CEventAgent.c

set CFLAGS=/MD /Zi /Ox /Os /Gy /c

mkdir %TARGET%
cd %TARGET%

:: build .obj
cl %CFLAGS% /I%VS_DIR%\lib\amd64 /I%VS_DIR%\include /I"%JAVA_HOME%\include" /I%SRC% /I"%JAVA_HOME%\include\win32" %SOURCE%
:: link to .dll
link -dll /LIBPATH:%VS_DIR%\lib\amd64 /LIBPATH:%WIN_LIB% -out:%LIBRARY% %OBJECT%
:: add manifest to .dll
"C:\Program Files\Microsoft SDKs\Windows\v7.0\Bin\mt.exe" -manifest %LIBRARY%.manifest -outputresource:%LIBRARY%;2

cd ../rsc