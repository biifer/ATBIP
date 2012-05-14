class GatewaysController < ApplicationController
  before_filter :authenticate_user!
  # GET /gateways
  # GET /gateways.json
  def index

    #------------------------------------------------------------------------
    # EDIT:
    #   Gets all gateways that belongs to the current user. 
    #------------------------------------------------------------------------

    #@gateways = Gateway.all
    @gateways = Gateway.where({ :owner => [current_user]}).all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @gateways }
    end
  end

  # GET /gateways/1
  # GET /gateways/1.json
  def show

    #------------------------------------------------------------------------
    # EDIT: 
    #   Gets the gateway with gateway/'ID' if the user owns that gateway else 
    # redirect to 'index' page. 
    #   Gets sensors that belongs to this Gateway.
    #------------------------------------------------------------------------

    @gateway = Gateway.find_by_id_and_owner(params[:id], current_user.id)

    if @gateway.nil?
      redirect_to :action=>'index'
    else  
       @sensor = @gateway.sensor  
      respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @gateway}
      format.json { render json: @sensor}
      end
    end
  end

  # GET /gateways/new
  # GET /gateways/new.json
  def new
    @gateway = Gateway.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @gateway.id }
    end
  end

  # GET /gateways/1/edit
  def edit

    #------------------------------------------------------------------------
    # EDIT: 
    #   Gets the gateway with gateway/'ID' if the user owns that gateway else 
    # redirect to 'index' page 
    #------------------------------------------------------------------------

    #@gateway = Gateway.find(params[:id])
    @gateway = Gateway.find_by_id_and_owner(params[:id], current_user.id)

    if @gateway.nil?
      redirect_to :action=>'index'
    else
    end
  end

  # POST /gateways
  # POST /gateways.json
  def create
    @gateway = Gateway.new(params[:gateway])

    @gateway.owner = current_user.id

    respond_to do |format|
      if @gateway.save

        format.html { redirect_to @gateway, notice: 'Gateway was successfully created.' }
        format.json { render json: @gateway, status: :created, location: @gateway }
      else
        format.html { render action: "new" }
        format.json { render json: @gateway.errors, status: :unprocessable_entity }
      end
    end

  end

  # PUT /gateways/1
  # PUT /gateways/1.json
  def update
    @gateway = Gateway.find(params[:id])

    respond_to do |format|
      if @gateway.update_attributes(params[:gateway])
        format.html { redirect_to @gateway, notice: 'Gateway was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @gateway.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /gateways/1
  # DELETE /gateways/1.json
  def destroy
    @gateway = Gateway.find(params[:id])
    @gateway.destroy

    respond_to do |format|
      format.html { redirect_to gateways_url }
      format.json { head :no_content }
    end
  end
end
