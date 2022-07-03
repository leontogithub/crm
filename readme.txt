1、关于登录
验证账号和密码
user user= sql语句
if user为null，说明账户和密码错误
如果user不为null，说明账户和密码正确
需要继续向下验证其他的字段信息

从user get得到
验证失效时间
验证锁定状态
验证浏览器IP地址是否有效