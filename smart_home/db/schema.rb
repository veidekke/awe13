# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20140410072631) do

  create_table "activities", :force => true do |t|
    t.string   "value"
    t.integer  "actor_id"
    t.integer  "rule_id"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "conditions", :force => true do |t|
    t.string   "operator",    :null => false
    t.integer  "rule_id"
    t.integer  "resource_id", :null => false
    t.string   "value",       :null => false
    t.datetime "created_at",  :null => false
    t.datetime "updated_at",  :null => false
  end

  create_table "people", :force => true do |t|
    t.string   "name"
    t.boolean  "current_user",       :default => false
    t.integer  "movement_sensor_id"
    t.datetime "created_at",                            :null => false
    t.datetime "updated_at",                            :null => false
  end

  create_table "priorities", :force => true do |t|
    t.integer  "priorityable_id"
    t.string   "priorityable_type"
    t.integer  "value"
    t.datetime "created_at",        :null => false
    t.datetime "updated_at",        :null => false
  end

  create_table "resources", :force => true do |t|
    t.string   "name"
    t.integer  "room_id"
    t.integer  "value"
    t.string   "type"
    t.string   "resource_type"
    t.string   "last_user",     :default => "Default"
    t.datetime "created_at",                           :null => false
    t.datetime "updated_at",                           :null => false
  end

  create_table "resources_rules", :force => true do |t|
    t.integer "resource_id"
    t.integer "rule_id"
  end

  create_table "rooms", :force => true do |t|
    t.string   "name"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "rules", :force => true do |t|
    t.string   "name"
    t.string   "status"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

end
