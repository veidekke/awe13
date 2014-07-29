class ApplicationController < ActionController::Base
  include ApplicationHelper

  before_filter :reset_flash
  
  protect_from_forgery
end
