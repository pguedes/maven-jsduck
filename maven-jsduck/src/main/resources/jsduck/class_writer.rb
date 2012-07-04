require 'jsduck/parallel_wrap'
require 'jsduck/logger'
require 'jsduck/stdout'
require 'fileutils'

module JsDuck

  # Writes class data into files in JSON or JSONP format or to STDOUT.
  class ClassWriter
    def initialize(exporter_class, relations, opts)
      @relations = relations
      @exporter = exporter_class.new(relations, opts)
      @parallel = ParallelWrap.new(:in_processes => opts.processes)
    end

    # Writes class data into given directory or STDOUT when dir == :stdout.
    #
    # Extension is either ".json" for normal JSON output
    # or ".js" for JsonP output.
    def write(dir, extension)
      dir == :stdout ? write_stdout : write_dir(dir, extension)
    end

    private

    def write_stdout
      json = @parallel.map(@relations.classes) {|cls| @exporter.export(cls) }.compact
      Stdout.instance.add(json)
    end

    def write_dir(dir, extension)
      FileUtils.mkdir(dir)
      @parallel.each(@relations.classes) do |cls|
        filename = dir + "/" + cls[:name] + extension
        Logger.instance.log("Writing docs", filename)
        json = @exporter.export(cls)
        # skip file if exporter returned nil
        if json
          if extension == ".json"
            JsonDuck.write_json(filename, json)
          elsif extension == ".js"
            JsonDuck.write_jsonp(filename, cls[:name].gsub(/\./, "_"), json)
          else
            throw "Unexpected file extension: #{extension}"
          end
        end
      end
    end

  end

end
