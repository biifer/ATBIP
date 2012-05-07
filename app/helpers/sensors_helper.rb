module SensorsHelper
	
	def getMin(sensor_data)
		@no_value = "No value"

		if sensor_data[0] != nil	
			@min = sensor_data[0].value.to_i

			sensor_data.each do |sensor_data| 
				if sensor_data.value.to_i < @min 
					@min = sensor_data.value.to_i
				end
			end
			return @min.to_s
		else
			return @no_value
		end
	end

	def getMax(sensor_data)
		#@no_value is not needed, it is already defined in the getMin method
		@no_value = "No value"
		if sensor_data[0] != nil
			@max = sensor_data[0].value.to_i

			sensor_data.each do |sensor_data| 
				if sensor_data.value.to_i > @max 
					@max = sensor_data.value.to_i
				end
			end
			return @max.to_s
		else
			return @no_value
		end

	end

	def getCurrent(sensor_data)
		if sensor_data[0] != nil
			return sensor_data.last.value
		else
			return @no_value
		end
		
	end

	def getAverage(sensor_data)
		if sensor_data[0] != nil
			@value = 0
			sensor_data.each do |sensor_data|
				@value += sensor_data.value.to_i
			end
			@average = @value/@sensor_data.length
			return @average
		else
			return @no_value
		end
	end

end
