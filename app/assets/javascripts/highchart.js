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
                type: 'spline',
                backgroundColor: 'transparent',

                                events: {

                    load: function() {

                        
                        // set up the updating of the chart each second

                        var series = this.series[0];
                        var seriesAverage = this.series[1];
                        var length = sensor_data.length;
                        var totalValue = 0;
                        var numberOfElements = sensor_data.length;
                        for (var i = 0; i < sensor_data.length; i++) {
                            totalValue += parseInt(sensor_data[i].value);
                        };

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
/*
                        setInterval(function() {
                           $.ajax({url : '/sensors/' + sensor_id + '.json', type : 'GET'}).success(function(new_data) {
                                //alert(data);
                                if(length != new_data.length){
                                    
                                    for (var newSensorNumber = (new_data.length - length); newSensorNumber > 0 ; newSensorNumber--) {
                                    nextElement = new_data.length - newSensorNumber;
                                    totalValue += parseInt(new_data[nextElement].value);
                                        
                                    var split1 = new_data[nextElement].created_at.split('T');
                                    var split2 = split1[1].split('Z');
                                    
                                    var x = new Date((split1[0] + ' ' +split2[0])).getTime(), // current time
                                    y = parseInt(new_data[nextElement].value);
                                    alert("Interval: " + x);
                                   series.addPoint([x, y], true, false);
                                   seriesAverage.addPoint([x, totalValue/nextElement], true, false);

                                   };

                                    length = new_data.length;
                                }
                            })

                        }, 10000);
*/
                    }

                }

            },

            title: {

                text: 'Temperature'

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

                    text: 'Temperature'

                },

                labels: {

                    formatter: function() {

                        return this.value +'°'

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

                name: 'Temperature',

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
