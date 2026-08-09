# 本项目已开源并且可能不会再更新。

这是一个由[horsenuggets (HorseNuggets)](https://github.com/horsenuggets)在2021年开发的项目，并由我在2022年fork并开始发展。中间经历了不到一年的开源在[keaidehuangpi/megawalls-hypixel: veryyyyyyy advanced Megawalls FFA plugin](https://github.com/keaidehuangpi/megawalls-hypixel)，随后原项目地址被archive，然后转向Gitee托管的闭源。

在它被放在服务器实例上运行的时间里，大部分时间都跑在mc.huiwow.top上，而后者几乎半公开地作为一个hvh服务器存在着，玩家群体很小，不超过百人。

这种情况持续到了2023年的暑假，然后大家开学了，然后作者上高中了，然后就这么结束了，就是这么简单。

时过境迁，我不得不承认，即使当时面对的最大的问题现在可以迎刃而解，这个插件连之前的光和热也不足以发出了。

同时，从另一方面来说，也已经出现了更好的MegawallsFFA替代品，其中甚至有因不明原因基于本插件的闭源版本发展而出的，即使我并没有授权--但这也并不重要了。

从技术角度评估，这个插件里面混杂的代码堪称古今中外，代码水平参差不齐，所以即使我近期作了一次重构的尝试，也认为这是没必要且困难的。

由于个人原因，我也无意再从事此类型项目的开发了。

综上所述，我遂决定开源，并将其托管回github上。这样它至少还有被再次发现的可能，即使这希望是微薄的；至少还能证明有一段时光确实存在过。

谨以此项目献给那个只会留存在回忆里的时代。

---

# 技术说明

## 请尽量使用本项目已上传至libs里的插件及服务端，否则可能会引发功能缺失或未知问题。

原因包括但可能不限于（这些是我能想起来的）：

1. NametagAPI插件是经过修改的（为了应付插件更改Nametag的需求）。请自行参考和比对[keaidehuangpi/NameTagAPIFORMWFFA](https://github.com/keaidehuangpi/NameTagAPIFORMWFFA)；

2. spigot.jar服务端经过修改（全部都是对NMS部分作出的）。其中包括：

   - net.minecraft.server.v1_8_R3.ItemMilkBucket中原生删除了返还桶的机制;

   - ItemPotion原生删除了返回药水的机制；

   - EntityWither删除了半血以下免疫箭矢的机制；

   - 对DataWatcher和EntityTrackerEntry两个类进行了修改，通过在数据发送的时候把血量放缩到合法范围内的方式修复了当凋灵血量太高时血条会溢出框内的问题。

3. Scoreboardplus插件是经过修改的，为了能在计分板里正常显示需要的Megawalls数据。呃，经过查找我当时似乎是从字节码修改的，所以还是自行比对吧。

###### //well，这实在是太酷了，很符合我对过去生活的想象，原始并带着野蛮，不是吗？

好吧，如果你告诉我可以有更好的方法实现上述操作，我认同你的观点。但正因为以上所提到的种种原因，我只能在这里把它们列出来，然后提醒你注意了。毕竟这也只算个“小时候写的代码”。

在那个还没有AI Coding的时代，一个未来的文科生开动脑筋能想出这么猎奇的办法，我还是相当佩服我自己的。

## ”先人未竟之事业“（如果你真的读了本项目的代码并感到好奇的话）

1. 平衡性问题：新加入的“马克兔”（MK2.kt）并没有以较好的平衡性融入游戏；新加入的各种Skyblock有关的物品（如Hyperion，Terminator）甚至没有合法获取渠道。

2. 自定义的实体和物品只搭了个框架，并没有真正发展起来。

3. swordNames和抽奖：本来是打算设定通过抽奖能改变武器名字和形态，然而抽奖实际上还没写，swordNames只写了个配置系统。

4. 引导不充分：很多新机制让人难以理解，没有充足的说明。

5. 特效缺失：自定义职业（Guardian，Driver,GoldenDragon）缺少特效。

6. 除此之外，你可以看到todo.txt，这是horsenuggets写的。在里面不乏一些完成了的，也有不少现在还没完成的；我就加了个“finish the Chinese translate”，然而实际上没有付诸过实践。

   > 呃，如果你要问我为什么，一是麻烦（那时候还没AI），二是我懒得切输入法，所以就一直这么写下去了。

7. 构建工具与项目结构：完全基于IDEA，没有使用现代化的gradle。奇异搞笑地，这也是我懒得搞造成的。

## 版权声明

由于此分支的开源版本自始至终采用MIT协议，而至今为止此分支已全部开源，这里的全部代码从发布之日起被用来进行的任何操作只需符合MIT协议即可。

# 附加

现在是凌晨两点，我治好了自己的精神内耗。

Apple 和 AirPods 是 Apple Inc. 在美国和其他国家和地区注册的商标。

(参考文献：[helang/README.MD at master · SAOKnight/helang](https://github.com/SAOKnight/helang/blob/master/README.MD))

---

以下是这个项目原来的README.MD部分

---

# megawalls-hypixel

veryyyyyyy advanced Megawalls FFA plugin

# NOTICE:

libs是运行目录 请把你的游戏在这下面运行并且使用那里的spigot.jar作为核心
libs里面包含的其它jar是软依赖 作为dev你要把它们加到你的依赖中 但如果没有测试需求的话你只需要加载libs/plugins里面的东西就可

### NO CONFIG.YML WILL BE PROVIDED.THEY WILL BE GENERATED WHEN THE PLUGIN LOADS.
