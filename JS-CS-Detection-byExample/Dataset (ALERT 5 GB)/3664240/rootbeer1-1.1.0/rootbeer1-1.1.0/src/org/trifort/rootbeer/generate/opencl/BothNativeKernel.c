
void org_trifort_syncthreads()
{
  org_trifort_barrier();
}

void org_trifort_threadfence()
{
}

void org_trifort_threadfence_block()
{
}

char *
org_trifort_gc_deref(char * gc_info, int handle){
  long long lhandle;
  long long * to_space;
  long space_size;
  long long array;
  long long offset;
  long long address;
  char * data_arr;

  lhandle = handle;
  lhandle = lhandle << 4;
  to_space = org_trifort_gc_get_to_space_address(gc_info);
  space_size = org_trifort_getlong(gc_info, 16);
  array = lhandle / space_size;
  offset = lhandle % space_size;

  address = to_space[array];
  data_arr = (char *) address;
  return &data_arr[offset];
}

int
org_trifort_gc_malloc(char * gc_info, int size){
  long long * addr;
  long long space_size;
  long long ret;
  int mod;
  long long start_array;
  long long end_array;

  addr = (long long *) (gc_info + TO_SPACE_FREE_POINTER_OFFSET);
  space_size = org_trifort_getlong(gc_info, 16);

  mod = size % 16;
  if(mod != 0){
    size += (16 - mod);
  }

  while(1){
    ret = atom_add(addr, (long) size);
    
    start_array = ret / space_size;
    end_array = (ret + size) / space_size;

    if(start_array != end_array){
      continue;
    }

    ret = ret >> 4;
    return (int) ret;
  }
}

int
org_trifort_classConstant(int type_num){
  return global_class_refs[type_num];
}

char *
org_trifort_gc_init(char * gc_info_space,
                         long long * to_space,
                         long long to_space_free_ptr,
                         long long space_size){

  org_trifort_setlong(gc_info_space, 0, (long long) to_space);
  org_trifort_setlong(gc_info_space, 8, to_space_free_ptr);
  org_trifort_setlong(gc_info_space, 16, space_size);
    
  return (char *) gc_info_space;
}
