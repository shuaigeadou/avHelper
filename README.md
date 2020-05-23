# avHelper
这个小程序的主要目的是配合embyserver管理AV，从网站批量下载av封面、预览图和影片信息。下载对应的影片信息后会保存成“演员名/番号/影片”这种结构。生成的info信息是embyserver可以识别的，其他影音管理软件没有尝试。

支持刮削网站：javbus、mgstage、fanza。（后面两个新加的，逻辑写的乱七八糟，图片切的也乱七八糟，最好还是用第一个）

运行需要在Windows下，jre7或以上。

对一个电影拆分成多个文件的情况不太好处理，暂时不支持在页面上修改文件名了，页面只是用来显示。如果根据规则修改的名称不正确，自己把文件名改成符合规则的或不要使用规则生成文件名了。或者去temp/movies.xml直接修改，所有的操作都是以movies.xml为根据，修改之前会在temp目录备份。

代码写的比较low，很多都是抄的网上的，抽象的也不好，有需要的自己改吧。本来想看看代码学学HTTPclient怎么用的，结果一抽风又去写刮削器了，HTTPclient还是不会用。估计以后不维护这个了。

这个是网上一大佬基于HttpClient-4.4.1封装的一个工具类，有需要的可以瞅瞅。[httpclientutil](https://github.com/Arronlong/httpclientutil)

下载：

[avHelper-2.3.0](https://github.com/shuaigeadou/avHelper/blob/master/%E7%89%88%E6%9C%AC/avHelper-2.3.0.zip?raw=true)

[教程](https://github.com/shuaigeadou/avHelper/blob/master/%E6%95%99%E7%A8%8B%EF%BC%88%E5%90%AB%E6%9C%89%E6%88%90%E4%BA%BA%E7%94%BB%E9%9D%A2%EF%BC%8C%E8%AF%B7%E7%A1%AE%E8%AE%A4%E5%B7%B2%E6%BB%A118%E5%B2%81%EF%BC%89.zip?raw=true)
