Auth::Application.routes.draw do
  resources :gateways

  resources :sensor_data

  resources :sensors

  resources :devices

  root to: "sessions#new"
  match "/auth/:provider/callback", to: "sessions#create"
  match "/auth/failure", to: "sessions#failure"
  match "/logout", to: "sessions#destroy", :as => "logout"
  resources :identities
end
