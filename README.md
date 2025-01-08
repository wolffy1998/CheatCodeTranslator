### RetroArch 金手指汉化工具

1. 需要在百度翻译开放平台申请文字翻译接口，并且获取API_KEY和SECRET_KEY两个参数。
2. 在src/main/java/com/example/fanyi/CheatCodeTranslatorGUI.java文件中填入申请的参数。

```java
private static final String API_KEY = "";
private static final String SECRET_KEY = "";
```

3. JDK_1.8.0_211，项目管理工具：Maven-3.6.3。
4. Maven编译打包jar文件后，可以使用exe4j9工具将jar包转成windows可执行文件。

