![](sjylogo.png)
# 下拉刷新库

### 基本介绍
####  android原生下拉刷新库，支持给各种原生控件添加下拉刷新,默认仿新浪微博下拉刷新，同时也支持自定义下拉刷新头部动画


### 如何使用

#### Android Studio
    第一步：
      在项目的gradle里配置
      allprojects {
      		repositories {
      			...
      			maven { url 'https://jitpack.io' }
      		}
      	}

      第二步：
      在module的gradle里配置
      dependencies {
      	        compile 'com.github.shajinyang:SPayUtil:1.0.0'
      	}

### 使用示例

#### 基本用法
    new AliPayHelper
        .PBuilder()
        .setAppId(stringHttpResult.data.getPARTNER())
        .setPid(stringHttpResult.data.getSELLER())
        .setRs2(stringHttpResult.data.getRSA_PRIVATE())
        .setBody("")
        .setSubject("测试")
        .setTotalMoney("0.01")
        .setTradeNo("637366373663736")
        .setTargetId("737363")
        .create()
        .pay(MainActivity.this);

#### 自定义下拉刷新头部
    new WxPayHelper
        .PBuilder()
        .setAppId("wxf8b4f85f3a794e77")
        .setNonceStr("测试")
        .setPartnerId("123243243")
        .setPrePayId("2324353454654")
        .setSign("424eerer3243dfsfer43")
        .create()
        .pay(MainActivity.this);








