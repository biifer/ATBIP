class SensorsController < ApplicationController
  before_filter :authenticate_user!
  # GET /sensors
  # GET /sensors.json
  def index
    @sensors = Sensor.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @sensors }
    end
  end

  # GET /sensors/1
  # GET /sensors/1.json
  def show
    @sensor = Sensor.find(params[:id])
    #------------------------------------------------------------
    #@sensor.sensor_reading can't return the readings because the 
    #sensors ID is compared with sensorReading sesor_id column
    #------------------------------------------------------------
    @sensor_data = SensorReading.where("sensor_id = ? AND gateway_id = ?", @sensor.sensor_id, @sensor.gateway_id)

    respond_to do |format|
      format.html # show.html.erb
      
      format.json { render json: @sensor_data }
      format.json { render json: @sensor }   
    end
  end

  # GET /sensors/new
  # GET /sensors/new.json
  def new
    @sensor = Sensor.new

    #------------------------------------------------------------------------
    # EDIT:
    #   Receives the id of the gateway and passes it to 'Sensor/View/new.html' 
    #------------------------------------------------------------------------

    @gateway_id = params[:gateway_id]

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @sensor }
      format.json { render json: @gateway_id }
    end
  end

  # GET /sensors/1/edit
  def edit
    @sensor = Sensor.find(params[:id])
    @gateway_id = @sensor.gateway_id
    respond_to do |format|
      format.html
      format.json { render json:  @gateway_id}
    end
  end

  # POST /sensors
  # POST /sensors.json
  def create
    @sensor = Sensor.new(params[:sensor])

    respond_to do |format|
      if @sensor.save
        format.html { redirect_to @sensor, notice: 'Sensor was successfully created.' }
        format.json { render json: @sensor, status: :created, location: @sensor }
      else
        format.html { render action: "new" }
        format.json { render json: @sensor.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /sensors/1
  # PUT /sensors/1.json
  def update
    @sensor = Sensor.find(params[:id])

    respond_to do |format|
      if @sensor.update_attributes(params[:sensor])
        format.html { redirect_to @sensor, notice: 'Sensor was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @sensor.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /sensors/1
  # DELETE /sensors/1.json
  def destroy
    @sensor = Sensor.find(params[:id])
    @sensor.destroy

    respond_to do |format|
      format.html { redirect_to gateway_path }
      format.json { head :no_content }
    end
  end
  def today
    @sensor = Sensor.find(params[:id])
    @sensor_data = SensorReading.where("sensor_id = ? AND gateway_id = ? AND created_at >= ?", @sensor.sensor_id, @sensor.gateway_id, Time.now.beginning_of_day)

    respond_to do |format|  
      format.json { render json: @sensor_data }
    end
  end
end
