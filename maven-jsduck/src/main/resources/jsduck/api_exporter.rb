require 'jsduck/json_duck'
require 'jsduck/class'

module JsDuck

  # Exporter for simple JSON format listing only class name and names
  # of all of its members.
  #
  # It produces the following structure:
  #
  # {
  #   :name => "Panel",
  #   :members => {
  #     :cfg => ["width", "height", "title"],
  #     :method => ["getWidth", "setWidth"],
  #     ...
  #   },
  #   :statics => { ... }
  # }
  #
  class ApiExporter
    def initialize(relations, opts)
      # All params ignored, they're present to be compatible with
      # other exporters.
    end

    # Returns hash of class name and member names
    def export(cls)
      {
        :name => cls[:name],
        :members => export_members(cls, :members),
        :statics => export_members(cls, :statics),
      }
    end

    private

    def export_members(cls, context)
      h = {}
      Class.default_members_hash.each_key do |type|
        h[type] = cls.members(type, context).map {|m| m[:name] }
      end
      h
    end

  end

end
