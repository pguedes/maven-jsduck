require "jsduck/tag/tag"
require "jsduck/js/utils"

module JsDuck::Tag
  class Override < Tag
    def initialize
      @pattern = "override"
      @tagname = :override
      @ext_define_pattern = "override"
    end

    # @override nameOfOverride
    def parse_doc(p, pos)
      if classname = p.ident_chain
        {
          :tagname => :override,
          :override => classname,
        }
      else
        # When @override not followed by class name, ignore the tag.
        # That's because the current ext codebase has some methods
        # tagged with @override to denote they override something.
        # But that's not what @override is meant for in JSDuck.
        nil
      end
    end

    def process_doc(h, tags, pos)
      h[:override] = tags[0][:override]
    end

    def parse_ext_define(cls, ast)
      cls[:override] = JsDuck::Js::Utils.make_string(ast)
    end

  end
end
