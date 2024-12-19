# Design Document

**Name**:Moon.

## <span id="jump1">.gitlet所包含的文件以及目录</span>

### objects(Dir)
这一个目录包含了众多以`03`,`7a`等编号首两个组成的目录，这是因为我们所用的Hash编码为`SHA-1`编码，将文件所有内容编码后获得了40为SHA-1编码，为减少文件查找的复杂度我们将其首两个字符提出并作为目录的名字，后38位作为文件本身的名字。

在原本的git中，应该具有tree，blob和commit三种对象同等的存在Objects目录下，但是在gitlet中简化到只在CWD下存储单纯的文件，所以无需tree对象，而为了高效率完成`global-log`方法，我选择在gitlet中将所有的`commit`对象都设置在同一个文件夹下面。

#### Commit(Dir)
用于存储commit对象，且直接将文件名设置为SHA-1哈希码。

### refs(Dir) 
refs文件夹用于存储Gitle仓库的引用，而这些引用都指向特定的提交，而这个refs目录之下的heads文件夹和remote文件夹分别对应着本地分支和远程分支的引用

#### heads
heads对应的是本地的仓库中的所有分支，如果我们有一个`master`和一个`develop`两个文件在里面，则对应着两个分支为`master`和`develop`。

需注意的是这存储的实际上也是一个Commit的SHA-1的id，只不过放在这里更具有结构性，通过HEAD定位到这里并读取文件内容得到id，则可以通过这个id找到Objects中存储的真实的Commit对象将其反序列化。

#### remotes
对应各个远程仓库，其中的每一个文件夹对应一个远程仓库，再往深的文件则对应仓库的分支

### INDEX(File) 
以一个文件模拟暂存区，可以定义一个类并将其序列化存入该文件

### HEAD(File)
存一个路径表示当前分支是对应的什么，如`refs/heads/master`

## Classes and Data Structures

### Class 1 Commit类
将每一个提交都看作一个对象，这个对象应该包含

1. parents(即对父节点的引用)

2. id(表示该提交对象的hashcode编码)

3. message(commit提交的时候带的类似于备注的信息)

4. timestamp(时间戳)

5. fileMap(一个Map，映射到对应的blob)

### Class 2 Blob类
每一个文件的不同版本都可以视为一个Blob，这个对象只包含`id`和`contents`两个成员，分别表示该类的hashcode和内容字符串

这个Blob类较为特殊，它是id和文本内容的映射，也正是它映射的是用户真正要存储的文件，所以才能以这样的形式存储下来，其本身的存储还是以sha-1编码存在`Objects`中的

### Class 3 Stage类
包含一个Map用于表示添加的文件极其对应的Blob和一个List用于表示删除的文件列表，因为一个文件可能对应多个Blob版本但是删除只有文件的对应所以无需用Map来存储

### Class 4 Utils
这个类是Proj提供的工具类，也可以自己定义静态方法在这里以供项目使用

#### readObject
接受一个文件对象和一个对象的class文件，返回一个对象。即从给出的文件中反序列化一个给出的相关对象出来，并将其作为返回值返回，因此将该方法赋值给一个对象实例即可以得到从文件中读取原来存储的实例对象的功能。

#### 

## Algorithms

### Init realization
为了实现gitlet中的初始化操作，这个方法定义在仓库类中并是静态方法。

如果`.gitlet`文件夹已经存在，则退出并显示相关信息

如果`.gitlet`文件夹不存在，则创建该文件夹并把相关的目录(参照 [.gitlet所包含的文件以及目录](#jump1)), 

创造完整个仓库文件夹之后需要创建第一次提交，即默认提交。

在成功创造了第一次提交之后需要同步把HEAD更新上去，所以我们需要在写一个update本地heads的方法并加以更新，由于HEAD中村的是一个字符串路径指向heads中的分支，所以我们使用writeContents方法并且求的其相对于`.gitlet`的相对路径将其转换为字符串写入HEAD中

### Add realization
判断该文件是否存在，不存在则需要给出错误信息

注：我们的`Blob`类型是在每次add的时候添加到Objects文件夹下的

按两种情况:

1.上一次的`commit`中也有这次提交的相同文件名，则需要按照对应的`contents`的SHA-1编码是否一致进行分类，若一致直接exit即可，若不一致则需要把该映射再`put`进去(因为java中相同的key对应后put进球的value所以直接put即可)

2.上一次的`commit`中没有这次提交相同的文件名，则只需要进行单纯的映射`put`即可

### Commit realization

### Checkout realization
分情况讨论有三种情况</Br>
1. 参数字符串数组长度为3 : 这种情况是将head commit的一个指定文件到CWD下并且将其替换
2. 参数字符串数组长度为4 : 这种是指定某一id的提交的指定文件并将其替换现有的CWD下的文件
3. 参数字符串数组长度为2 : 切换到指定分支，即用指定分支head commit替换CWD，并覆盖当前的文件版本，并将该指定分支修改为当前分支，即更新HEAD。<Br>
在这个过程中如果当前CWD下暂存区非空，则除非更换的分支为当前分支，则将清空暂存区。再如果检查到有一个未被跟踪的且同名(会引发冲突)的文件则需要退出并打印错误信息，即让用户删除此文件或者将其添加并提交。(若做解释的话即未被跟踪的文件是要保留的，但因为转到目标分支有相同文件的话会引起转换后有两个相同文件的情况--即冲突)但若是非同名且未被跟踪则会选择保留，这是因为git中也会不管这个CWD下不被跟踪的文件，将其保留。<Br>
现在要做的是转换当前目录到目标分支，那么就首先

### Merge realization

终于到了最后的合并算法，这一步后我们的提交集终于从一个简单的序列发展成一棵树之后再最终发展成为了一张有向无环图。

#### 失败情况
1. 判断暂存区是否清空
2. 判断要合并的该分支是否存在
3. 判断要合并的分支与当前分支对应的Commit是否相同，相同则无需合并
4. 判断是否有要被合并的文件未被跟踪，同理于之前的`checkout`操作

#### 合并情况
1. 当分裂点为当前分支的头，这个时候目标分支的版本在基于当前分支的后面，所以会直接`checkout`到目标分支并且输出对应信息
2. 当分裂点为目标分支的头，则当前分支的版本在目标分支的后面，即什么都不做并且输出对应信息
> ps：在这之后的情况都是把合并后的版本存入暂存区等待commit，这与git的逻辑一致，而上述两个情况都无需进行什么更改，因此不需要存入暂存区；上述两种不涉及产生新的Commit版本所以不需要暂存，而下面的几种合理的合并方式都会对CWD进行更改并且最后合成新的Commit版本，所以需要暂存以供Commit
3. 在目标分支中修改过而没有在当前分支中修改过的文件，应该暂存为目标分支中的版本。
4. 在目标分支中没有修改而在当前分支修改过的文件，则应保留为当前分支的版本。
5. 如果在两个分支中一个文件被相同的修改或者都被删除，则在新的提交中仍然是这样，如果都被删除了那么哪怕CWD中有也是未被跟踪状态，不放在新提交中
8. 在分裂点存在且当前分支未修改且目标分支中不存在的都应该删除
9. 在分裂点存在且目标分支未修改且当前分支不存在的都应该保持不存在
10. 在当前分支和目标分支都进行不同修改的会引起冲突

## Persistence

## Style

文本文件在POSIX标准中在最后一行需要换行符，即空出最后一行，这是因为许多工具和编译器都期望文本文件是这样格式化的，这在本Proj中也有`Style`上的要求

对于`<>`,`()`这些括号中，分割参数之间一般以`, `即逗号再加空格的形式

对于命名，有`camelCase`的标准要求，即不能以下划线开头，且要以小写字母开头，对于静态变量，要全用大写字母且以下划线分隔。

同一行不能有过多字符，一般不可超过100个，可以进行以下改进
方法调用链拆分
```
// 原始代码（超出长度限制）
String result = someObject.someMethod(arg1, arg2).anotherMethod(arg3, arg4).finalMethod();

// 拆分后
String result = someObject.someMethod(arg1, arg2)
                          .anotherMethod(arg3, arg4)
                          .finalMethod();

```

长条件语句拆分
```
// 原始代码（超出长度限制）
if (condition1 && condition2 && condition3 && condition4 && condition5) {
    doSomething();
}

// 拆分后
if (condition1 && condition2 
        && condition3 && condition4 
        && condition5) {
    doSomething();
}

```

字符串拼接换行
```
// 原始代码（超出长度限制）
String message = "This is a very long message that exceeds the character limit for a single line.";

// 拆分后
String message = "This is a very long message that " +
                 "exceeds the character limit for a single line.";

```

多参数调用拆分
```
// 原始代码（超出长度限制）
someMethod(param1, param2, param3, param4, param5, param6, param7);

// 拆分后
someMethod(param1, param2, param3, 
           param4, param5, param6, 
           param7);

```

if,while,for等跟()的语句都要与()之间间隔一个空格。
