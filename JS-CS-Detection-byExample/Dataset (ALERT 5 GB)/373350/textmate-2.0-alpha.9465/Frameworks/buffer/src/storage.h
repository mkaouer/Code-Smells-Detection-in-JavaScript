#ifndef BUFFER_H_FCAAENIG
#define BUFFER_H_FCAAENIG

#include <oak/basic_tree.h>
#include <oak/misc.h>
#include <oak/debug.h>

namespace ng
{
	namespace detail
	{
		struct memory_t
		{
			WATCH_LEAKS(ng::detail::memory_t);

			struct helper_t
			{
				helper_t (size_t size) : _size(size) { _bytes = (char*)malloc(size + 15); }
				~helper_t ()                         { ::free(_bytes); }
				char* bytes ()                       { return _bytes; }
				size_t size () const                 { return _size; }
				size_t free () const                 { return malloc_size(_bytes) - _size; }
				void grow (size_t amount)            { _size += amount; }

			private:
				char* _bytes;
				size_t _size;
			};

			typedef std::shared_ptr<helper_t> helper_ptr;

			template <typename _InputIter>
			memory_t (_InputIter first, _InputIter last);

			memory_t () : _offset(0)                          { }
			memory_t (helper_ptr const& helper, size_t offset) : _helper(helper), _offset(offset) { }
			memory_t subset (size_t from)                     { return memory_t(_helper, _offset + from); }
			char const* bytes () const                        { return _helper->bytes() + _offset; }
			size_t size () const                              { return _helper->size() - _offset; }
			size_t free () const                              { return _helper->free(); }

			template <typename _InputIter>
			void insert (size_t pos, _InputIter first, _InputIter last);

		private:
			helper_ptr _helper;
			size_t _offset;
		};

		struct PUBLIC storage_t
		{
			WATCH_LEAKS(ng::detail::storage_t);

			struct value_t
			{
				value_t (memory_t const& memory, size_t size) : _memory(memory), _size(size) { }

				char const* data () const   { return _memory.bytes(); }
				size_t size () const        { return _size; }

				char const* begin () const  { return data(); }
				char const* end () const    { return data() + size(); }

			private:
				memory_t const& _memory;
				size_t _size;
			};

			struct iterator : public std::iterator<std::bidirectional_iterator_tag, value_t>
			{
				iterator (typename oak::basic_tree_t<size_t, memory_t>::iterator base) : _base(base) { }
				iterator (iterator const& rhs) : _base(rhs._base) { }
				iterator& operator= (iterator const& rhs)   { _base = rhs._base; return *this; }

				bool operator== (iterator const& rhs) const { return _base == rhs._base; }
				bool operator!= (iterator const& rhs) const { return _base != rhs._base; }
				iterator& operator-- ()                     { --_base; return *this; }
				iterator& operator++ ()                     { ++_base; return *this; }
				value_t operator* () const                  { return value_t(_base->value, _base->key); }

			private:
				typename oak::basic_tree_t<size_t, memory_t>::iterator _base;
			};

			size_t size () const       { return _tree.aggregated(); }
			bool empty () const        { return _tree.empty(); }
			void swap (storage_t& rhs) { _tree.swap(rhs._tree); }
			void clear ()              { _tree.clear(); }

			iterator begin () const    { return iterator(_tree.begin()); }
			iterator end () const      { return iterator(_tree.end());   }

			void insert (size_t pos, char const* data, size_t length);
			void erase (size_t first, size_t last);
			char operator[] (size_t i) const;
			std::string substr (size_t first, size_t last) const;

		private:
			typedef oak::basic_tree_t<size_t, memory_t> tree_t;
			mutable tree_t _tree;
			tree_t::iterator split_at (tree_t::iterator, size_t pos);
			tree_t::iterator find_pos (size_t pos) const;
		};

	} /* detail */

} /* ng */

#endif /* end of include guard: BUFFER_H_FCAAENIG */
