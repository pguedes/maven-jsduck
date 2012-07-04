require 'jsduck/logger'
require 'pp'

module JsDuck

  # Deals with inheriting documentation
  class InheritDoc
    def initialize(relations)
      @relations = relations
    end

    # Performs all inheriting
    def resolve_all
      @relations.each do |cls|
        resolve_class(cls) if cls[:inheritdoc]
        cls.all_local_members.each do |member|
          if member[:inheritdoc]
            resolve(member)
          end
        end
      end
    end

    # Copy over doc/params/return from parent member.
    def resolve(m)
      parent = find_parent(m)
      m[:doc] = (m[:doc] + "\n\n" + parent[:doc]).strip
      m[:params] = parent[:params] if parent[:params]
      m[:return] = parent[:return] if parent[:return]
    end

    # Finds parent member of the given member.  When @inheritdoc names
    # a member to inherit from, finds that member instead.
    #
    # If the parent also has @inheritdoc, continues recursively.
    def find_parent(m)
      if m[:inheritdoc][:cls]
        # @inheritdoc MyClass#member
        parent_cls = @relations[m[:inheritdoc][:cls]]
        return warn("class not found", m) unless parent_cls

        parent = lookup_member(parent_cls, m)
        return warn("member not found", m) unless parent

      elsif m[:inheritdoc][:member]
        # @inheritdoc #member
        parent = lookup_member(@relations[m[:owner]], m)
        return warn("member not found", m) unless parent

      else
        # @inheritdoc
        parent_cls = @relations[m[:owner]].parent
        mixins = @relations[m[:owner]].mixins

        # Warn when no parent or mixins at all
        return warn("parent class not found", m) unless parent_cls || mixins.length > 0

        # First check for the member in all mixins, because members
        # from mixins override those from parent class.  Looking first
        # from mixins is probably a bit slower, but it's the correct
        # order to do things.
        if mixins.length > 0
          parent = mixins.map {|mix| lookup_member(mix, m) }.compact.first
        end

        # When not found, try looking from parent class
        if !parent && parent_cls
          parent = lookup_member(parent_cls, m)
        end

        # Only when both parent and mixins fail, throw warning
        return warn("parent member not found", m) unless parent
      end

      return parent[:inheritdoc] ? find_parent(parent) : parent
    end

    def lookup_member(cls, m)
      inherit = m[:inheritdoc]
      cls.get_members(inherit[:member] || m[:name], inherit[:type] || m[:tagname], inherit[:static] || m[:meta][:static])[0]
    end

    # Copy over doc from parent class.
    def resolve_class(cls)
      parent = find_class_parent(cls)
      cls[:doc] = (cls[:doc] + "\n\n" + parent[:doc]).strip
    end

    def find_class_parent(cls)
      if cls[:inheritdoc][:cls]
        # @inheritdoc MyClass
        parent = @relations[cls[:inheritdoc][:cls]]
        return warn("class not found", cls) unless parent
      else
        # @inheritdoc
        parent = cls.parent
        return warn("parent class not found", cls) unless parent
      end

      return parent[:inheritdoc] ? find_class_parent(parent) : parent
    end

    def warn(msg, item)
      context = item[:files][0]
      i_member = item[:inheritdoc][:member]

      msg = "@inheritdoc #{item[:inheritdoc][:cls]}"+ (i_member ? "#" + i_member : "") + " - " + msg
      Logger.instance.warn(:inheritdoc, msg, context[:filename], context[:linenr])

      return item
    end

  end

end
