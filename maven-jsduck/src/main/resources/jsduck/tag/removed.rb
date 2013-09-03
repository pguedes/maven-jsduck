require "jsduck/tag/deprecated_tag"

module JsDuck::Tag
  # To document members that were present in previous version but are
  # completely gone now.  Other than that it behaves exactly like
  # @deprecated.
  class Removed < DeprecatedTag
    def initialize
      @tagname = :removed
      # striked-through text with red border.
      @css = <<-EOCSS
        .signature .removed {
          color: #aa0000;
          background-color: transparent;
          border: 1px solid #aa0000;
          text-decoration: line-through;
        }
        .removed-box strong {
          color: #aa0000;
          border: 1px solid #aa0000;
          background-color: transparent;
          text-decoration: line-through;
        }
      EOCSS
      super
    end
  end
end
