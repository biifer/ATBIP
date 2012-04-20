class SensorDataController < ApplicationController
  # GET /sensor_data
  # GET /sensor_data.json
  def index
    @sensor_data = SensorDatum.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @sensor_data }
    end
  end

  # GET /sensor_data/1
  # GET /sensor_data/1.json
  def show
    @sensor_datum = SensorDatum.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @sensor_datum }
    end
  end

  # GET /sensor_data/new
  # GET /sensor_data/new.json
  def new
    @sensor_datum = SensorDatum.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @sensor_datum }
    end
  end

  # GET /sensor_data/1/edit
  def edit
    @sensor_datum = SensorDatum.find(params[:id])
  end

  # POST /sensor_data
  # POST /sensor_data.json
  def create
    @sensor_datum = SensorDatum.new(params[:sensor_datum])

    respond_to do |format|
      if @sensor_datum.save
        format.html { redirect_to @sensor_datum, notice: 'Sensor datum was successfully created.' }
        format.json { render json: @sensor_datum, status: :created, location: @sensor_datum }
      else
        format.html { render action: "new" }
        format.json { render json: @sensor_datum.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /sensor_data/1
  # PUT /sensor_data/1.json
  def update
    @sensor_datum = SensorDatum.find(params[:id])

    respond_to do |format|
      if @sensor_datum.update_attributes(params[:sensor_datum])
        format.html { redirect_to @sensor_datum, notice: 'Sensor datum was successfully updated.' }
        format.json { head :ok }
      else
        format.html { render action: "edit" }
        format.json { render json: @sensor_datum.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /sensor_data/1
  # DELETE /sensor_data/1.json
  def destroy
    @sensor_datum = SensorDatum.find(params[:id])
    @sensor_datum.destroy

    respond_to do |format|
      format.html { redirect_to sensor_data_url }
      format.json { head :ok }
    end
  end
end
