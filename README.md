
## 过程记录
0. 引入依赖
1. 拷贝代码生成器过来创建实体类和service,mapper,controller等等
2. 拷贝common包过来
3. 拷贝config包过来
4. 拷贝shiro 包过来
5. 拷贝util  包过来
6. 拷贝application.yml过来
7. 编写controller
8. 拷贝swagger2的config类

## 说明
1. 很多status是数字,每个数字代表不同含义
2. 偷懒把很多业务逻辑卸载了controller层,有空在改到service层吧... 😔
3. 老是要在controller层和service/serviceImpl层跳来跳去很麻烦欸
4. 用了很多断言,真的太香了,之前怎么不会用啊
5. pagesize该改成size,pagenum改成current
6. 还要判断size的大小 //if (size>20) return Result.fail();
7. 使用redis除了引入依赖还要配置对象序列化策略,添加缓存还没学
    - redisTemplate.opsForValue();//操作字符串
    - redisTemplate.opsForHash();//操作hash
    - redisTemplate.opsForList();//操作list
    - redisTemplate.opsForSet();//操作set
    - redisTemplate.opsForZSet();//操作有序set
8. post有发布者的意思,用来指代点赞发起人
9. 点赞请求包含被点赞资源类型,id,uid,和post_id
10. redis加锁要学习下
11. uid指user_id(用户主键),aid指article_id,资源用用`item`表示
12. 对于资源类型`item_type`用数字枚举,1代表翻译2代表评论3代表文章
13. 对于状态`status`1代表可以用2代表禁止/冻结3代表删除
14. 七牛云的基本使用
    1. 创建bucket,并用自己的子域名来解析CNAME
    2. CDN板块>普通域名>`res.domain.com`其他默认
    3. 买域名的地方,域名控制台→域名解析,添加记录,输入CNAME
    4. pom引入sdk
15. 积分功能  
    1. 使用aop,After Returning通知添加积分
    

## 变更
1. controller和service中获取uid不该解析再解析toke来获取 应该用subject
2. Assert应该返回200 然后根据msg进行提示 还可以弄一个201-error.vue 或者alert,然后$router.go(-1)