#!/bin/sh

# export ETCDCTL_API=2
HOST=127.0.0.1
ENDPOINTS=http://$HOST:8000
URI=$ENDPOINTS/i18n/test
JSON='{"text":"a","email":"codezone@163.com"}'
echo 'Test for ' $URI
echo

echo "============================="

echo "测试 1 Accept-Language:zh-CN"
echo "测试 1 URI:"$URI
curl -w '\n' \
-H 'Accept-Language: zh-CN,zh;q=0.5' --compressed \
-H 'Content-Type: application/json' \
-H 'Origin: http://localhost:8000' --data-raw '{"text":"a","email":"codezone@163.com"}' \
$URI

echo
echo "测试 2 Accept-Language:en-US"
echo "测试 2 URI:"$URI
curl -w '\n' \
-H 'Content-Type: application/json' \
-H 'Accept-Language: en-US,en;q=0.5' --compressed \
-H 'Origin: http://localhost:8000' --data-raw $JSON \
$URI

echo

echo "测试 3 Accept-Language:zh-CN 在Params中设置lang为en_US"
echo "测试 3 URI:"$URI'?lang=en_US'
echo "测试 3 Header:"'Accept-Language: zh-CN,zh;q=0.5'
curl -w '\n' \
-H 'Content-Type: application/json' \
-H 'Accept-Language: zh-CN,zh;q=0.5' --compressed \
-X POST -d $JSON \
$URI'?lang=en_US'

echo
echo "测试 4 Accept-Language:en_US 在Params中设置lang为 zh-CN"
echo "测试 4 URI:"$URI'?lang=zh-CN'
echo "测试 4 Header:"'Accept-Language: en-US,en;q=0.5'
curl -w '\n' \
-H 'Content-Type: application/json' \
-H 'Accept-Language: en-US,en;q=0.5' --compressed \
-X POST -d $JSON \
$URI'?lang=zh-CN'

echo
echo "Tips:"
echo "我们可能会看到出错的情况，原因是spring boot 默认使用了 AcceptHeaderLocaleResolver,该默认值不允许更改语言环境。"
echo "要解决这个问题，请尝试在Spring bean配置文件中声明一个SessionLocaleResolver bean，它在大多数情况下应该是合适的。"