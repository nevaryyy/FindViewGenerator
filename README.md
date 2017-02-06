## FindViewGenerator
一个基于AndroidStudio的插件，用于自动生成Activity和Fragment的View，与FindView搭配使用。
## FindView 
通过注解自动地对Activity和Fragment的View进行实例化，无需手动调用findViewById。详见[https://github.com/nevaryyy/FindView]()。
## 使用方法
1. 打开AndroidStudio的File-Settings-Plugins，点击Browse repositories，搜索Android FindView Generator并安装。或者直接下载本项目根目录下的jar包，使用install plugin from disk进行安装。
2. 安装完成后，若AndroidStudio的顶部的Code菜单栏的第一项为FindView Generator，即安装成功。
3. 采用FindView的方法完成对layout的编码后，在相应的Activity或Fragment中，点击Code-FindView Generator或快捷键Alt+F，即会在当前Activity或Fragment中自动生成相应的View代码。

## LICENSE
MIT LICENSE
