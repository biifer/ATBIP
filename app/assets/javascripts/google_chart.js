$(function () {
  $(document).ready(function() {



    $.get("/sensors/"+ sensor_url_id +".json").success(function(sensor_data) {
      if(sensor_type == 'Temperature'){
        var options = {
          greenColor: '#5C67FA',
          greenFrom: -40, greenTo: -20,
          yellowFrom: 30, yellowTo: 40,
          redFrom: 40, redTo: 50,
          minorTicks: 1, 
          min: -40,
          max: 50,
        };
      }else if(sensor_type == 'Weight'){
        var options = {

          minorTicks: 1, 
          min: 0,
          max: 150,
        };
      }

      var length = sensor_data.length;
      var minGaugeData = new google.visualization.DataTable();
      minGaugeData.addColumn('number', 'Min ' + sensor_unit);
      minGaugeData.addRows(1);
      minGaugeData.setCell(0, 0, parseInt(min_value));

      var maxGaugeData = new google.visualization.DataTable();
      maxGaugeData.addColumn('number', 'Max ' + sensor_unit);
      maxGaugeData.addRows(1);
      maxGaugeData.setCell(0, 0, parseInt(max_value));

      var currentGaugeData = new google.visualization.DataTable();
      currentGaugeData.addColumn('number', 'Current ' + sensor_unit);
      currentGaugeData.addRows(1);
      currentGaugeData.setCell(0, 0, parseInt(sensor_data[ length - 1 ].value));
    
        var max_gauge_chart = new google.visualization.Gauge(document.getElementById('max_gauge_div'));
        var min_gauge_chart = new google.visualization.Gauge(document.getElementById('min_gauge_div'));
        var current_gauge_chart = new google.visualization.Gauge(document.getElementById('current_gauge_div'));

        max_gauge_chart.draw(maxGaugeData, options);
        min_gauge_chart.draw(minGaugeData, options);
        current_gauge_chart.draw(currentGaugeData, options);

      $("button#today").click(function(){
        $.get("/sensors/today/"+ sensor_url_id +".json").success(function(sensor_data_today) {
          if(sensor_data_today.length == 0){
            alert("No values from today!");
          }else{
          max = parseInt(sensor_data_today[0].value);
          min = parseInt(sensor_data_today[0].value);

          for (var i = 1; i < sensor_data_today.length; i++) {
            if(parseInt(sensor_data_today[i].value) > max){
              max = parseInt(sensor_data_today[i].value);
            }
            else if(parseInt(sensor_data_today[i].value) < min){
              min = parseInt(sensor_data_today[i].value);
            };
          minGaugeData.setValue(0, 0, min);
          min_gauge_chart.draw(minGaugeData, options);
          maxGaugeData.setValue(0, 0, max);
          max_gauge_chart.draw(maxGaugeData, options);
          };
        };
        });
      });
        

        $("button#all").click(function(){
          minGaugeData.setValue(0, 0, min_value);
          min_gauge_chart.draw(minGaugeData, options);
          maxGaugeData.setValue(0, 0, parseInt(max_value));
          max_gauge_chart.draw(maxGaugeData, options);
        });

      var faye = new Faye.Client('http://biifer.mine.nu:9292/faye');
      faye.subscribe("/sensor/" + sensor_id + "/new", function(object) {
        currentGaugeData.setValue(0, 0, parseInt(object.value));
        current_gauge_chart.draw(currentGaugeData, options);
        if (parseInt(object.value) < parseInt(min_value)){
          min_value = parseInt(object.value);
          minGaugeData.setValue(0, 0, parseInt(object.value));
          min_gauge_chart.draw(minGaugeData, options);
        }else if(parseInt(object.value) > parseInt(max_value)){
          max_value = parseInt(object.value);
          maxGaugeData.setValue(0, 0, parseInt(object.value));
          max_gauge_chart.draw(maxGaugeData, options);
        };
      });
    });
  });
})
