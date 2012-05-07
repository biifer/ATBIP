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

        $.get("/sensors/"+ sensor_id +".json").success(function(sensor_data) {
            


            // Or do whatever you want :)
            

        chart = new Highcharts.Chart({   

            chart: {

                renderTo: 'highchart',
                zoomType: 'x',
                type: 'spline',

                                events: {

                    load: function() {

                        
                        // set up the updating of the chart each second

                        var series = this.series[0];
                        var seriesAverage = this.series[1];
                        var length = sensor_data.length;
                        var totalValue = 0;
                        var nextValue;
                        for (var i = 0; i < sensor_data.length; i++) {
                            totalValue += parseInt(sensor_data[i].value);
                        };
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
                                   series.addPoint([x, y], true, true);
                                   seriesAverage.addPoint([x, totalValue/nextElement], true, true);

                                   };

                                    length = new_data.length;
                                }
                            })

                        }, 10000);

                    }

                }

            },

            title: {

                text: 'Temperature'

            },

            subtitle: {

                text: 'Source (Sensor ID): ' + sensor_data[0].sensor_id

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

                    data: (function() {

                    // generate an array of random data

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
