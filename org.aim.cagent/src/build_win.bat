@REM
@REM Copyright 2014 SAP AG
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

:: %Visual Studio%\VC\lib,
:: %Visual Studio%\VC\redist\<System>\Microsoft.VC<Version>.CRT and
:: %Microsoft SDKs$\Windows\<Version>\Lib\<System>
:: have to be specified in the PATH variable.
:: (e.g.,
:: C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\lib\amd64
:: C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\redist\amd64\Microsoft.VC90.CRT
:: C:\Program Files\Microsoft SDKs\Windows\v7.0\Lib\x64 )
:: Ensure that Visual Studio and Java are either both x86 or both x64!

:: Has to be set depending on the installation of Visual Studio and the system (x86 / x64)
set VS_DIR="C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC"
set VS_LIB=%VS_DIR%\lib\amd64
set WIN_SDK="C:\Program Files\Microsoft SDKs\Windows\v7.0"
set WIN_LIB=%WIN_SDK%\Lib\x64

set TARGET=..\target
set SRC=..\src
set LIBRARY=libCEventAgent.dll
set OBJECT=CEventAgent.obj
set SOURCE=%SRC%\CEventAgent.c

set CFLAGS=/MD /Zi /Ox /Os /Gy /c

mkdir %TARGET%
cd %TARGET%

:: build .obj
cl %CFLAGS% /I%VS_LIB% /I%VS_DIR%\include /I"%JAVA_HOME%\include" /I%SRC% /I"%JAVA_HOME%\include\win32" %SOURCE%
:: link to .dll
link -dll /LIBPATH:%VS_LIB% /LIBPATH:%WIN_LIB% -out:%LIBRARY% %OBJECT%
:: add manifest to .dll
%WIN_SDK%\Bin\mt.exe -manifest %LIBRARY%.manifest -outputresource:%LIBRARY%;2

cd ..\rsc