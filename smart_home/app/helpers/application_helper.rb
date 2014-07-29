# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
  def flash_class(level)
    case level
        when :notice then "alert alert-info"
        when :success then "alert alert-success"
        when :error then "alert alert-danger"
        when :alert then "alert alert-danger"
    end
  end

  def reset_flash
    puts "reset_flash"
    flash[:notice] = nil
    flash[:success] = nil
    flash[:error] = nil
    flash[:alert] = nil
  end

  def get_status_color(status)
    case status
      when "sleeping" then "alert-warning"
      when "active" then "alert-success"
      when "waiting" then "alert-warning"
      when "active/waiting" then "alert-warning"
    end
  end

end
