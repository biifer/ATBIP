$(function () {
    var chart;
    var data_table = [];
    var date_table = [];
    var average_table = [];
    var average = 0;
    var temp = 0;
    $(document).ready(function() {

        $.get("/sensors/75.json").success(function(sensor_data) {
            
            for(var i = 0; i < sensor_data.length; i++) {
                data_table[i] = parseInt(sensor_data[i].value);
                var split1 = sensor_data[i].created_at.split('T');
                var split2 = split1[1].split('Z');
                date_table[i] = split1[0] + ' ' +split2[0];
                average += parseInt(sensor_data[i].value);
            };
            average = average/sensor_data.length
            for (var i = 0; i < sensor_data.length; i++) {
                temp += data_table[i]
                average_table[i] = temp/(i+1);
            };

            // Or do whatever you want :)
            

        chart = new Highcharts.Chart({

            chart: {

                renderTo: 'highchart',
                zoomType: 'x',
                type: 'spline'

            },

            title: {

                text: 'Temperature'

            },

            subtitle: {

                text: 'Source (Sensor ID): ' + sensor_data[0].sensor_id

            },

            xAxis: {

                categories: date_table

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

                marker: {

                    symbol: 'circle'

                },

                data: data_table

            },{
                name: 'Average',

                marker: {

                    enabled: false

                },
                dashStyle: 'shortdot',
                
  
            data: average_table

                /*data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, {

                    y: 26.5,

                    marker: {

                        symbol: 'url(http://www.highcharts.com/demo/gfx/sun.png)'

                    }

                }, 23.3, 18.3, 13.9, 9.6]

        

            }, {

                name: 'London',

                marker: {

                    symbol: 'diamond'

                },

                data: [{

                    y: 3.9,

                    marker: {

                        symbol: 'url(http://www.highcharts.com/demo/gfx/snow.png)'

                    }

                }, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
*/
            }]

        });

    });
});
})
