Rails.application.config.middleware.use OmniAuth::Builder do
  provider :twitter, ENV['lgU5EboRifXMyapqCaVzNw'], ['DbyTpieKrQqdq1BqtUrITC5t54siNXUI3XokceI']
  provider :facebook, ENV['key'], ENV['secret']
  provider :google_oauth2, ENV['key'], ENV['secret']
  provider :identity, on_failed_registration: lambda { |env|
      IdentitiesController.action(:new).call(env)
  }
end