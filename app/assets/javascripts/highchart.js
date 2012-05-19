$(function () {

    var chart;
    var data_table = [];
    var date_table = [];
    var average_table = [];
    var average = 0;
    var temp = 0;
    $(document).ready(function() {

                Highcharts.setOptions({

            global: {

                useUTC: false

            }

        });

        $.get("/sensors/"+ sensor_url_id +".json").success(function(sensor_data) {
            


            // Or do whatever you want :)
            

        chart = new Highcharts.Chart({   

            chart: {

                renderTo: 'highchart',
                zoomType: 'x',
                type: 'line',
                backgroundColor: 'transparent',

                                events: {

                    load: function() {

                        
                        // set up the updating of the chart each second

                        var series = this.series[0];
                        var seriesAverage = this.series[1];
                        var length = sensor_data.length;
                        var totalValue = 0;
                        var numberOfElements = sensor_data.length;
                        var readingRange = null;
                        for (var i = 0; i < sensor_data.length; i++) {
                            totalValue += parseInt(sensor_data[i].value);
                        };

                        $("button#today").click(function(){
                            if(readingRange != 'today'){
                                readingRange = 'today';
                            $.get("/sensors/today/"+ sensor_url_id +".json").success(function(sensor_data_today) {
                                if(sensor_data_today.length == 0){
                                    
                                }else{
                                    while(series.data.length != 0){
                                        series.data[0].remove(true);
                                        seriesAverage.data[0].remove(true);
                                    };
                                    total = 0;

                            
                               
                                    for (var i = 0; i < sensor_data_today.length; i++) {
                                
                                        var split1 = sensor_data_today[i].created_at.split('T');
                                        var split2 = split1[1].split('Z');
                                        total += parseInt(sensor_data_today[i].value);
                                        var x = new Date((split1[0] + ' ' +split2[0])).getTime(),
                                        y = parseInt(sensor_data_today[i].value);
                                        series.addPoint([x,y], true, false);
                                        seriesAverage.addPoint([x, total/(i+1)], true, false);
                                    };
                                };
                            });
                            };                      
                        });

                        $("button#all").click(function(){
                            if(readingRange != 'all'){
                                readingRange = 'all';
                            $.get("/sensors/"+ sensor_url_id +".json").success(function(sensor_data) {
                            var i = 0;
                            while(series.data.length != 0){
                                series.data[0].remove(true);
                                seriesAverage.data[0].remove(true);
                            };
                            total = 0;

                            for (var i = 0; i < sensor_data.length; i++) {
                                var split1 = sensor_data[i].created_at.split('T');
                                var split2 = split1[1].split('Z');
                                var readingsTime = new Date((split1[0] + ' ' +split2[0]));
                                total += parseInt(sensor_data[i].value);

                                var x = new Date((split1[0] + ' ' +split2[0])).getTime(),
                                y = parseInt(sensor_data[i].value);
                                series.addPoint([x,y], true, false);
                                seriesAverage.addPoint([x,total/(i+1)], true, false);
                                
                            };
                            });
                        };
                        });

                        var faye = new Faye.Client('http://biifer.mine.nu:9292/faye');
                       // alert(sensor_data[0].sensor_id);
                        faye.subscribe("/sensor/" + sensor_id + "/new", function(object) {
                        
                            var x = new Date(object.created_at).getTime(),
                            y = parseInt(object.value);
                            totalValue += y;
                            numberOfElements++;
                            series.addPoint([x, y], true, false);
                            seriesAverage.addPoint([x, totalValue/numberOfElements], true, false);
                        });

                    }

                }

            },

            title: {

                text: sensor_type

            },

            subtitle: {

                text: 'Source (Sensor ID): ' + sensor_url_id

            },

            xAxis: {

            //    categories: date_table
                type: 'datetime',
                tickPixelInterval: 150

            },

            yAxis: {

                title: {

                    text: sensor_type

                },

                labels: {

                    formatter: function() {
 
                        return this.value + sensor_unit
     

                    }

                }

            },

            tooltip: {

                crosshairs: true,

                shared: true

            },

            plotOptions: {

                spline: {

                    marker: {

                        radius: 4,

                        lineColor: '#666666',

                        lineWidth: 1

                    }

                }

            },



            series: [{

                name: sensor_type,

                    data: (function() {

                    // generate an array of random data

                    var data = [],

                        i;


            

                    for (i = 0; i < sensor_data.length; i++) {
                        var split1 = sensor_data[i].created_at.split('T');
                        var split2 = split1[1].split('Z');
                        //date_table[i] = split1[0] + ' ' +split2[0];
                        data.push({

                            x: new Date((split1[0] + ' ' +split2[0])),

                            y: parseInt(sensor_data[i].value)

                        });

                    }

                    return data;

                })()

            },
            {
             //Average data! 
             
                name: 'Average',

                marker: {
                    enabled: false
                },

                dashStyle: 'shortdot',

                    data: (function() {

                    var data = [],
                        avg = 0,
                        i;

                    for (i = 0; i < sensor_data.length; i++) {
                        var split1 = sensor_data[i].created_at.split('T');
                        var split2 = split1[1].split('Z');

                        avg += parseInt(sensor_data[i].value)

                        data.push({

                            x: new Date((split1[0] + ' ' +split2[0])),

                            y: avg/(i+1)

                        });

                    }

                    return data;

                })()

            }]

        });

    });
});
})
