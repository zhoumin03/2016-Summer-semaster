1.目录结构
根目录是一个java工程；
根目录下的home文件夹是一个示例,为了显示xml中的路径;
data文件夹下的template文件夹是网页的模板文件夹,对于不同工程的网页，只改变了template/data/data.js；
data文件夹下的test文件夹下包含了xml文件

2.template文件夹结构:
Ace 			-- web code editor
semantic 		-- semantic UI 前端布局
jqueryFileTree 	-- 文件树的实现
js 				-- javascript文件, 包含主要的控制文件main.js
css				-- 层叠样式表文件, main.css中设置了Marker和active path的样式
data			-- data.js储存可变的数据

3.main.js代码的说明
initControl()			--入口函数
initEditor()			--初始化editor，设置一些属性
initFileTree()			--初始化文件树，绑定文件路径和点击事件
initFaultsSet()			--读入缺陷路径，并根据筛选要求进行筛选，添加点击事件
initFaultsFilter()		--定义了checkbox的onchange事件
initPath()				--将path的内容清空
loadFile()				--根据路径得到代码，设置代码，去除以前的所有markers和annotations
loadFaultPath()			--fault表格里元素的点击事件。加载一条缺陷路径，并默认打开第一个节点的文件
removeOldMarkers()		--去除所有之前的markers
addMarker()				--去掉之前active元素的样式，触发点击事件的元素加上active样式，加载文件，加上annotation和marker, scroll到marker的行

4.java部分
主要功能就是将得到工程文件的信息和report xml的信息，再写到data.js中