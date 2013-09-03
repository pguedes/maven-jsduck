require "jsduck/tag/member_tag"
require "jsduck/doc/subproperties"

module JsDuck::Tag
  class Cfg < MemberTag
    def initialize
      @pattern = "cfg"
      @tagname = :cfg
      @repeatable = true
      @member_type = {
        :title => "Config options",
        :toolbar_title => "Configs",
        :position => MEMBER_POS_CFG,
        :icon => File.dirname(__FILE__) + "/icons/cfg.png",
        :subsections => [
          {:title => "Required config options", :filter => {:required => true}},
          {:title => "Optional config options", :filter => {:required => false}, :default => true},
        ]
      }
    end

    # @cfg {Type} [name=default] (required) ...
    def parse_doc(p, pos)
      tag = p.standard_tag({
          :tagname => :cfg,
          :type => true,
          :name => true,
          :default => true,
          :optional => true
        })

      # don't parse (required) after subproperties
      unless tag[:name] =~ /\./
        tag[:optional] = false if parse_required(p)
      end

      tag[:doc] = :multiline
      tag
    end

    def parse_required(p)
      p.hw.match(/\(required\)/i)
    end

    def process_doc(h, tags, pos)
      p = tags[0]
      h[:type] = p[:type]
      h[:default] = p[:default]
      h[:required] = true if p[:optional] == false

      # Documentation after the first @cfg is part of the top-level docs.
      h[:doc] += p[:doc]

      nested = JsDuck::Doc::Subproperties.nest(tags, pos)[0]
      h[:properties] = nested[:properties]
      h[:name] = nested[:name]
    end

    def process_code(code)
      h = super(code)
      h[:type] = code[:type]
      h[:default] = code[:default]
      h[:accessor] = code[:accessor]
      h[:evented] = code[:evented]
      h
    end

    # Do the merging of :type field
    def merge(h, docs, code)
      if h[:type] == nil
        h[:type] = code[:tagname] == :method ? "Function" : "Object"
      end
    end

    def to_html(cfg, cls)
      member_link(cfg) + " : " + cfg[:html_type]
    end
  end
end
