devenv quickfix.sln /build Debug
devenv quickfix.sln /build Release

pushd examples
devenv examples.sln /build Debug
devenv examples.sln /build Release
popd

pushd src\java
devenv quickfix_jni.sln /build Debug
devenv quickfix_jni.sln /build Release
popd