Rails.application.config.middleware.use OmniAuth::Builder do
  provider :twitter, 'lgU5EboRifXMyapqCaVzNw', 'DbyTpieKrQqdq1BqtUrITC5t54siNXUI3XokceI'
  #provider :facebook, 'key', 'secret'
end