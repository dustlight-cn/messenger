# Messenger
[简介](#简介) | [部署](#部署)

## 简介
消息与通知服务

## 部署
> 此服务依赖 MongoDB 以及 RabbitMQ。

### Helm 部署
选择此部署方式必须先安装 [Helm](https://helm.sh)。  
请查看 Helm 的 [文档](https://helm.sh/docs) 获取更多信息。

当 Helm 安装完毕后，使用下面命令添加仓库：

    helm repo add messenger https://dustlight-cn.github.io/messenger

若您已经添加仓库，执行命令 `helm repo update` 获取最新的包。
您可以通过命令 `helm search repo messenger` 来查看他们的 charts。

创建配置文件 values.yaml：
```yaml
ingress:
  className: "nginx" # Ingress Class 名称
  host: "messenger.dustlight.cn" # 服务域名
  tls:
    enabled: false # 是否开启 HTTPS
    crt: ""
    key: ""

config:
  mongo:
    uri: "" # MongoDB 连接地址
  rabbitmq:
    host: "" # RabbitMQ 连接地址
    port: 5672 # RabbitMQ 连接端口
    username: "" # RabbitMQ 连接用户名
    password: "" # RabbitMQ 连接用户名
  mail:
    host: "" # SMTP 服务器地址
    username: "" # SMTP 用户名（邮箱）
    password: "" # SMTP 密码
```

安装：

    helm install -f values.yaml my-messenger messenger/messenger-service

卸载：

    helm delete my-messenger

## 依赖

### RabbitMQ

[RabbitMQ 部署文档](https://www.rabbitmq.com/kubernetes/operator/operator-overview.html)

### MongoDB

[MongoDB 部署文档](https://github.com/mongodb/mongodb-kubernetes-operator)

