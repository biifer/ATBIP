require 'test_helper'

class SensorDataControllerTest < ActionController::TestCase
  setup do
    @sensor_datum = sensor_data(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:sensor_data)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create sensor_datum" do
    assert_difference('SensorDatum.count') do
      post :create, sensor_datum: @sensor_datum.attributes
    end

    assert_redirected_to sensor_datum_path(assigns(:sensor_datum))
  end

  test "should show sensor_datum" do
    get :show, id: @sensor_datum.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @sensor_datum.to_param
    assert_response :success
  end

  test "should update sensor_datum" do
    put :update, id: @sensor_datum.to_param, sensor_datum: @sensor_datum.attributes
    assert_redirected_to sensor_datum_path(assigns(:sensor_datum))
  end

  test "should destroy sensor_datum" do
    assert_difference('SensorDatum.count', -1) do
      delete :destroy, id: @sensor_datum.to_param
    end

    assert_redirected_to sensor_data_path
  end
end
