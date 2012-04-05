Rails.application.config.middleware.use OmniAuth::Builder do
  provider :twitter, 'lgU5EboRifXMyapqCaVzNw', 'DbyTpieKrQqdq1BqtUrITC5t54siNXUI3XokceI'
  provider :google_oauth2, ENV['GOOGLE_KEY'], ENV['GOOGLE_SECRET']
  provider :facebook, 'FACEBOOK_ID', 'FACEBOOK_SECRET'
  provider :identity, on_failed_registration: lambda { |env|
    IdentitiesController.action(:new).call(env)
  }
end
