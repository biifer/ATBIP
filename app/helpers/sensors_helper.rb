module SensorsHelper
	
	def getMin(sensor_data)
		@no_value = "No value"

		if sensor_data[0] != nil	
			@min = sensor_data[0].value

			sensor_data.each do |sensor_data| 
				if sensor_data.value < @min 
					@min = sensor_data.value
				end
			end
			return @min
		else
			return @no_value
		end
	end

	def getMax(sensor_data)
		#@no_value is not needed, it is already defined in the getMin method
		@no_value = "No value"
		if sensor_data[0] != nil
			@max = sensor_data[0].value

			sensor_data.each do |sensor_data| 
				if sensor_data.value > @max 
					@max = sensor_data.value
				end
			end
			return @max
		else
			return @no_value
		end

	end

end
