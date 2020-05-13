layui.use('laydate', function () {
    var laydate = layui.laydate;

    //执行一个laydate实例
    laydate.render({
        elem: '#duringTime' //指定元素
        , type: 'datetime'
        , range: '~' //或 range: '~' 来自定义分割字符
        , min: '2020-3-1'
        , max: 1 // 时间最大选择当天的第二天
    });
});