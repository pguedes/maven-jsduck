require 'jsduck/logger'

module JsDuck

  # Reports bugs and problems in documentation
  class Lint
    attr_accessor :relations

    def initialize(relations)
      @relations = relations
    end

    # Runs the linter
    def run
      warn_no_doc
      warn_unnamed
      warn_optional_params
      warn_duplicate_params
      warn_duplicate_members
    end

    # print warning for each member or parameter with no name
    def warn_unnamed
      each_member do |member|
        if !member[:name] || member[:name] == ""
          warn(:name_missing, "Unnamed #{member[:tagname]}", member)
        end
        (member[:params] || []).each do |p|
          if !p[:name] || p[:name] == ""
            warn(:name_missing, "Unnamed parameter", member)
          end
        end
      end
    end

    # print warning for each class or public member with no name
    def warn_no_doc
      @relations.each do |cls|
        if cls[:doc] == ""
          warn(:no_doc, "No documentation for #{cls[:name]}", cls)
        end
      end
      each_member do |member|
        if member[:doc] == "" && !member[:private] && !member[:meta][:hide]
          warn(:no_doc, "No documentation for #{member[:owner]}##{member[:name]}", member)
        end
      end
    end

    # print warning for each non-optional parameter that follows an optional parameter
    def warn_optional_params
      each_member do |member|
        if member[:tagname] == :method
          optional_found = false
          member[:params].each do |p|
            if optional_found && !p[:optional]
              warn(:req_after_opt, "Optional param followed by regular param #{p[:name]}", member)
            end
            optional_found = optional_found || p[:optional]
          end
        end
      end
    end

    # print warnings for duplicate parameter names
    def warn_duplicate_params
      each_member do |member|
        params = {}
        (member[:params] || []).each do |p|
          if params[p[:name]]
            warn(:dup_param, "Duplicate parameter name #{p[:name]}", member)
          end
          params[p[:name]] = true
        end
      end
    end

    # print warnings for duplicate member names
    def warn_duplicate_members
      @relations.each do |cls|
        members = {:members => {}, :statics => {}}
        cls.all_local_members.each do |m|
          group = (m[:meta] && m[:meta][:static]) ? :statics : :members
          type = m[:tagname]
          name = m[:name]
          hash = members[group][type] || {}
          if hash[name]
            warn(:dup_member, "Duplicate #{type} name #{name}", hash[name])
            warn(:dup_member, "Duplicate #{type} name #{name}", m)
          end
          hash[name] = m
          members[group][type] = hash
        end
      end
    end

    # Loops through all members of all classes
    def each_member(&block)
      @relations.each {|cls| cls.all_local_members.each(&block) }
    end

    # Prints warning + filename and linenumber from doc-context
    def warn(type, msg, member)
      context = member[:files][0]
      Logger.instance.warn(type, msg, context[:filename], context[:linenr])
    end

  end

end
