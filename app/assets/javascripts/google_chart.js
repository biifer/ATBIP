$(function () {

  $(document).ready(function() {

    $.get("/sensors/"+ sensor_id +".json").success(function(sensor_data) {
    var length = sensor_data.length;
    setInterval(function() {
    
      $.ajax({url : '/sensors/' + sensor_id + '.json', type : 'GET'}).success(function(new_data) {
      if(length != new_data.length){
        length = new_data.length;

        for (var i = 0; i <= length - 1; i++) {
          var new_value = parseInt(new_data[i].value);
          if(max_value < new_value){
            max_value = new_value;
          };
          if(min_value > new_value){
            min_value = new_value;
          };
        };
        
        var options = {
          greenColor: '#5C67FA',
          greenFrom: -40, greenTo: -20,
          yellowFrom: 30, yellowTo: 40,
          redFrom: 40, redTo: 50,
          minorTicks: 1, 
          min: -40,
          max: 50,
        };
        var maxGaugeData = google.visualization.arrayToDataTable([
          ['Label', 'Value'],
          ['Max °C', max_value]
        ]);
        var minGaugeData = google.visualization.arrayToDataTable([
          ['Label', 'Value'],
          ['Min °C', min_value]
        ]);
        var currentGaugeData = google.visualization.arrayToDataTable([
          ['Label', 'Value'],
          ['Current °C', parseInt(new_data[new_data.length - 1].value)]
        ]);

        var max_gauge_chart = new google.visualization.Gauge(document.getElementById('max_gauge_div'));
        var min_gauge_chart = new google.visualization.Gauge(document.getElementById('min_gauge_div'));
        var current_gauge_chart = new google.visualization.Gauge(document.getElementById('current_gauge_div'));

        max_gauge_chart.draw(maxGaugeData, options);
        min_gauge_chart.draw(minGaugeData, options);
        current_gauge_chart.draw(currentGaugeData, options);
      }
    })
    }, 10000);    
  });
  });
})
