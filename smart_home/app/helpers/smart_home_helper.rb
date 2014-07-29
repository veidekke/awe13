module SmartHomeHelper
  def input_correct?(input)
    puts input.inspect
    resource_correct = input[:resource_id] ? !input[:resource_id].empty? : !input[:actor_id].empty?
    operator_correct = input[:operator] ? !input[:operator].empty? : true
    value_correct = !input[:value].empty?

    resource_correct && operator_correct && value_correct
  end

  def set_current_user_to_default
    current_user = Person.where(current_user: true).first
    if current_user.id != 1
      current_user.current_user = false
      current_user.save
      default_person = Person.find(1)
      default_person.current_user = true
      default_person.save
    end
  end

  def update_current_user(person_id)
    current_user_old = Person.where(current_user: true).first
    current_user_old.current_user = false
    current_user_old.save

    current_user_new = Person.find(person_id)
    current_user_new.current_user = true
    current_user_new.save
  end
end