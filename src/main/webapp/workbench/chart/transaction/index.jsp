<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + request.getContextPath() + "/";
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%--        根据交易表中不同阶段的数量进行一个统计，形成一个漏斗图（倒三角）
                阶段数量多的往上面排列
                阶段数量少的往下面排列
--%>

<head>
    <title>Title</title>
    <base href="<%=basePath%>">
    <script src="ECharts/echarts.min.js"></script>
    <script src="jquery/jquery-1.11.1-min.js"></script>

    <script>

        $(function () {
            //页面加载完毕后，绘制图表
            getCharts();
        })

        function getCharts() {

            $.ajax({
                url: "workbench/transaction/getCharts.do",
                type: "get",
                dataType: "json",
                success: function (data) {

                    var name = [];
                    var value = [];

                   for (var i=0;i<data.datalist.length;i++){
                       // alert(data.datalist[i].name)
                        name[i] = data.datalist[i].name;
                        value[i] = data.datalist[i].value;
                   }

                    var myChart = echarts.init(document.getElementById('main'));


                    var option = {
                        xAxis: {
                            type: 'category',
                            data: name
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [
                            {
                                data:  value,
                                type: 'bar',
                                showBackground: true,
                                backgroundStyle: {
                                    color: 'rgba(180, 180, 180, 0.2)'
                                }
                            }
                        ]
                    };

                    myChart.setOption(option);


  /*                     var myChart = echarts.init(document.getElementById('main'));

                 var option = {
                        title: {
                            text: '交易漏斗图',
                            subtext: "统计交易阶段数量的漏斗图"
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: '{a} <br/>{b} : {c}%'
                        },
                        toolbox: {
                            feature: {
                                dataView: {readOnly: false},
                                restore: {},
                                saveAsImage: {}
                            }
                        },
                        legend: {
                            data: ['Show', 'Click', 'Visit', 'Inquiry', 'Order']
                        },
                        series: [
                            {
                                name: '交易漏斗图',
                                type: 'funnel',
                                left: '10%',
                                top: 60,
                                bottom: 60,
                                width: '80%',
                                min: 0,
                                max: data.total,
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,
                                label: {
                                    show: true,
                                    position: 'inside'
                                },
                                labelLine: {
                                    length: 10,
                                    lineStyle: {
                                        width: 1,
                                        type: 'solid'
                                    }
                                },
                                itemStyle: {
                                    borderColor: '#fff',
                                    borderWidth: 1
                                },
                                emphasis: {
                                    label: {
                                        fontSize: 20
                                    }
                                },
                                data: data.datalist
                                /!*[
                                    {value: 60, name: '01资质审查'},
                                    {value: 40, name: 'Inquiry'},
                                    {value: 20, name: 'Order'},
                                    {value: 80, name: 'Click'},
                                    {value: 100, name: 'Show'}
                                ]*!/
                            }
                        ]
                    };

                    myChart.setOption(option);*/



                }

            })


        }

    </script>

</head>
<body>

<div id="main" style="width: 600px;height:400px;"></div>

</body>
</html>
